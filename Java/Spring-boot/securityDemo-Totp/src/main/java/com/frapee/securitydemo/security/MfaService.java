package com.frapee.securitydemo.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

/**
 * Implements RFC 6238 TOTP with RFC 4226 dynamic truncation, using only JDK
 * cryptography and encoding utilities.
 *
 * <p>Each demonstration user receives an independent random secret at
 * application startup. The secrets are intentionally held in memory and are
 * not suitable for production credential storage.</p>
 */
@Service
public class MfaService {

	private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
	private static final String HMAC_ALGORITHM = "HmacSHA1";
	private static final long TIME_STEP_SECONDS = 30;
	private static final int OTP_DIGITS = 6;
	private static final int OTP_MODULUS = 1_000_000;
	private static final int ACCEPTED_TIME_DRIFT_STEPS = 1;

	private final SecureRandom secureRandom = new SecureRandom();
	private final Map<String, String> demoSecrets;

	/**
	 * Generates independent secrets for the known in-memory demo accounts.
	 */
	public MfaService() {
		Map<String, String> secrets = new HashMap<>();
		for (String username : new String[] { "bla", "user", "admin", "author" }) {
			secrets.put(username, generateSecret());
		}
		demoSecrets = Map.copyOf(secrets);
	}

	/**
	 * Returns the in-memory TOTP secret assigned to a demonstration user.
	 *
	 * @param username authenticated demonstration username
	 * @return the unpadded Base32 secret
	 * @throws IllegalArgumentException if the username has no demo secret
	 */
	public String getSecretForUser(String username) {
		String secret = demoSecrets.get(username);
		if (secret == null) {
			throw new IllegalArgumentException("No TOTP secret is configured for this user");
		}
		return secret;
	}

	/**
	 * Generates a six-digit TOTP value for the supplied instant.
	 *
	 * @param base32Secret shared secret in Base32
	 * @param instant instant at which to generate the code
	 * @return six-digit decimal TOTP value, including leading zeroes
	 * @throws IllegalArgumentException if the secret is not valid Base32
	 * @throws NullPointerException if {@code instant} is null
	 */
	public String generateCode(String base32Secret, Instant instant) {
		Objects.requireNonNull(instant, "instant must not be null");
		long counter = Math.floorDiv(instant.getEpochSecond(), TIME_STEP_SECONDS);
		return codeForCounter(base32Secret, counter);
	}

	/**
	 * Verifies a submitted code against the current time step and one adjacent
	 * step on either side to tolerate small clock differences.
	 *
	 * @param base32Secret shared secret in Base32
	 * @param submittedCode user-entered six-digit code
	 * @param instant current time
	 * @return {@code true} only when the submitted code matches an accepted step
	 * @throws IllegalArgumentException if the secret is not valid Base32
	 * @throws NullPointerException if {@code instant} is null
	 */
	public boolean verifyCode(String base32Secret, String submittedCode, Instant instant) {
		Objects.requireNonNull(instant, "instant must not be null");
		if (submittedCode == null || !submittedCode.matches("[0-9]{6}")) {
			return false;
		}

		long currentCounter = Math.floorDiv(instant.getEpochSecond(), TIME_STEP_SECONDS);
		byte[] submittedBytes = submittedCode.getBytes(StandardCharsets.US_ASCII);
		boolean valid = false;
		for (long drift = -ACCEPTED_TIME_DRIFT_STEPS; drift <= ACCEPTED_TIME_DRIFT_STEPS; drift++) {
			long counter = currentCounter + drift;
			if (counter >= 0) {
				byte[] expectedBytes = codeForCounter(base32Secret, counter).getBytes(StandardCharsets.US_ASCII);
				valid |= MessageDigest.isEqual(expectedBytes, submittedBytes);
			}
		}
		return valid;
	}

	/**
	 * Generates a high-entropy secret and encodes it as unpadded Base32.
	 *
	 * @return a new 160-bit Base32 secret
	 */
	private String generateSecret() {
		byte[] secretBytes = new byte[20];
		secureRandom.nextBytes(secretBytes);
		return encodeBase32(secretBytes);
	}

	private static String codeForCounter(String base32Secret, long counter) {
		byte[] secret = decodeBase32(base32Secret);
		if (secret.length == 0) {
			throw new IllegalArgumentException("TOTP secret must not be empty");
		}

		byte[] message = ByteBuffer.allocate(Long.BYTES).putLong(counter).array();
		try {
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
			byte[] digest = mac.doFinal(message);
			int offset = digest[digest.length - 1] & 0x0f;
			int binary = ((digest[offset] & 0x7f) << 24)
					| ((digest[offset + 1] & 0xff) << 16)
					| ((digest[offset + 2] & 0xff) << 8)
					| (digest[offset + 3] & 0xff);
			int otp = binary % OTP_MODULUS;
			return String.format(java.util.Locale.ROOT, "%0" + OTP_DIGITS + "d", otp);
		} catch (GeneralSecurityException exception) {
			throw new IllegalStateException("JDK does not provide the required HMAC algorithm", exception);
		} finally {
			Arrays.fill(secret, (byte) 0);
		}
	}

	private static byte[] decodeBase32(String encoded) {
		if (encoded == null || encoded.isBlank()) {
			throw new IllegalArgumentException("TOTP secret must be a non-empty Base32 string");
		}

		String normalized = encoded.toUpperCase(java.util.Locale.ROOT);
		byte[] decoded = new byte[(normalized.length() * 5) / 8];
		int buffer = 0;
		int bitsInBuffer = 0;
		int outputIndex = 0;
		for (int index = 0; index < normalized.length(); index++) {
			char character = normalized.charAt(index);
			if (character == '=') {
				continue;
			}
			int value = BASE32_ALPHABET.indexOf(character);
			if (value < 0) {
				throw new IllegalArgumentException("TOTP secret contains a non-Base32 character");
			}
			buffer = (buffer << 5) | value;
			bitsInBuffer += 5;
			if (bitsInBuffer >= Byte.SIZE) {
				bitsInBuffer -= Byte.SIZE;
				decoded[outputIndex++] = (byte) (buffer >> bitsInBuffer);
				buffer &= (1 << bitsInBuffer) - 1;
			}
		}
		return Arrays.copyOf(decoded, outputIndex);
	}

	private static String encodeBase32(byte[] bytes) {
		StringBuilder encoded = new StringBuilder((bytes.length * Byte.SIZE + 4) / 5);
		int buffer = 0;
		int bitsInBuffer = 0;
		for (byte value : bytes) {
			buffer = (buffer << Byte.SIZE) | (value & 0xff);
			bitsInBuffer += Byte.SIZE;
			while (bitsInBuffer >= 5) {
				bitsInBuffer -= 5;
				encoded.append(BASE32_ALPHABET.charAt((buffer >> bitsInBuffer) & 0x1f));
				buffer &= (1 << bitsInBuffer) - 1;
			}
		}
		if (bitsInBuffer > 0) {
			encoded.append(BASE32_ALPHABET.charAt((buffer << (5 - bitsInBuffer)) & 0x1f));
		}
		return encoded.toString();
	}
}
