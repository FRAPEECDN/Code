package com.frapee.securitydemo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class MfaServiceTests {

	private static final String RFC_6238_SHA1_SECRET = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";

	private final MfaService mfaService = new MfaService();

	@Test
	void generatesRfc6238SixDigitTestVectors() {
		assertEquals("287082", mfaService.generateCode(RFC_6238_SHA1_SECRET, Instant.ofEpochSecond(59)));
		assertEquals("081804", mfaService.generateCode(RFC_6238_SHA1_SECRET, Instant.ofEpochSecond(1_111_111_109)));
		assertEquals("050471", mfaService.generateCode(RFC_6238_SHA1_SECRET, Instant.ofEpochSecond(1_111_111_111)));
		assertEquals("005924", mfaService.generateCode(RFC_6238_SHA1_SECRET, Instant.ofEpochSecond(1_234_567_890)));
		assertEquals("279037", mfaService.generateCode(RFC_6238_SHA1_SECRET, Instant.ofEpochSecond(2_000_000_000)));
		assertEquals("353130", mfaService.generateCode(RFC_6238_SHA1_SECRET, Instant.ofEpochSecond(20_000_000_000L)));
	}

	@Test
	void verifiesCodesWithSmallClockDriftAndRejectsMalformedCodes() {
		Instant instant = Instant.ofEpochSecond(59);
		String adjacentStepCode = mfaService.generateCode(RFC_6238_SHA1_SECRET, Instant.ofEpochSecond(89));

		assertTrue(mfaService.verifyCode(RFC_6238_SHA1_SECRET, adjacentStepCode, instant));
		assertFalse(mfaService.verifyCode(RFC_6238_SHA1_SECRET, "12345", instant));
		assertFalse(mfaService.verifyCode(RFC_6238_SHA1_SECRET, "12x456", instant));
		assertFalse(mfaService.verifyCode(RFC_6238_SHA1_SECRET, null, instant));
	}

	@Test
	void generatesSeparateSecretsForDemoAccounts() {
		String userSecret = mfaService.getSecretForUser("user");
		String adminSecret = mfaService.getSecretForUser("admin");

		assertEquals(32, userSecret.length());
		assertNotEquals(userSecret, adminSecret);
	}
}
