package foundation.jdbc.repository;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-GCM credential encryption using a passphrase-derived key.
 *
 * <p>The salt is public configuration; the passphrase must be supplied separately. Do not reuse an
 * encryption nonce with the same key. New nonces are generated automatically by {@link #encrypt(String)}.
 */
public final class CredentialCipher {
    private static final int ITERATIONS = 310_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int NONCE_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecretKeySpec key;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Derives a 256-bit AES key from a passphrase and a Base64-encoded salt.
     *
     * @param secret passphrase, which must not be stored beside the ciphertext
     * @param saltBase64 Base64 salt containing at least 16 bytes
     */
    public CredentialCipher(String secret, String saltBase64) {
        Objects.requireNonNull(secret, "secret");
        byte[] salt = Base64.getDecoder().decode(Objects.requireNonNull(saltBase64, "saltBase64"));
        if (salt.length < 16) {
            throw new IllegalArgumentException("credential encryption salt must be at least 16 bytes");
        }
        this.key = deriveKey(secret, salt);
    }

    /**
     * Encrypts a value and returns Base64 ciphertext and its unique nonce.
     *
     * @param plaintext UTF-8 value to encrypt
     * @return ciphertext formatted as {@code ENC(...)} and its Base64 nonce
     */
    public EncryptedValue encrypt(String plaintext) {
        Objects.requireNonNull(plaintext, "plaintext");
        byte[] nonce = new byte[NONCE_LENGTH_BYTES];
        secureRandom.nextBytes(nonce);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return new EncryptedValue(
                    "ENC(" + Base64.getEncoder().encodeToString(encrypted) + ")",
                    Base64.getEncoder().encodeToString(nonce));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to encrypt database credentials", exception);
        }
    }

    /**
     * Authenticates and decrypts one value.
     *
     * @param encryptedValue Base64 ciphertext, optionally wrapped in {@code ENC(...)}
     * @param nonceBase64 Base64 12-byte nonce belonging to that ciphertext
     * @return decrypted UTF-8 value
     * @throws IllegalStateException if authentication fails or the key is incorrect
     */
    public String decrypt(String encryptedValue, String nonceBase64) {
        Objects.requireNonNull(encryptedValue, "encryptedValue");
        Objects.requireNonNull(nonceBase64, "nonceBase64");
        String ciphertext = encryptedValue;
        if (ciphertext.startsWith("ENC(") && ciphertext.endsWith(")")) {
            ciphertext = ciphertext.substring(4, ciphertext.length() - 1);
        }
        byte[] nonce = Base64.getDecoder().decode(nonceBase64);
        if (nonce.length != NONCE_LENGTH_BYTES) {
            throw new IllegalArgumentException("credential encryption nonce must be 12 bytes");
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new IllegalStateException("Unable to decrypt database credentials; check the secret and encrypted config", exception);
        }
    }

    private static SecretKeySpec deriveKey(String secret, byte[] salt) {
        PBEKeySpec keySpec = new PBEKeySpec(secret.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        try {
            byte[] keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(keySpec)
                    .getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to derive database credential key", exception);
        } finally {
            keySpec.clearPassword();
        }
    }

    /**
     * Encrypted value fields intended for storage in the YAML config.
     *
     * @param ciphertext Base64 AES-GCM ciphertext, tagged as {@code ENC(...)}
     * @param nonce Base64 12-byte nonce used for this value
     */
    public record EncryptedValue(String ciphertext, String nonce) {}
}