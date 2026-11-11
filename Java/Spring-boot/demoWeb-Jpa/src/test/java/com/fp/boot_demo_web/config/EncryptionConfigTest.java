package com.fp.boot_demo_web.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/** Tests the encryptor configuration used for database credentials. */
class EncryptionConfigTest {

    /** Verifies encrypted credentials can be recovered with the same key. */
    @Test
    void encryptorCanRoundTripDatabaseCredentials() {
        var encryptor = EncryptionConfig.createEncryptor("test-only-key");
        String encrypted = encryptor.encrypt("test-database-secret");

        assertNotEquals("test-database-secret", encrypted);
        assertEquals("test-database-secret", encryptor.decrypt(encrypted));
    }
}
