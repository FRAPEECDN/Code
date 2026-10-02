package foundation.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import foundation.jdbc.repository.CredentialCipher;
import foundation.jdbc.repository.EncryptedDatabaseConfig;

class CredentialCipherTest {
    private static final String SALT = "AAECAwQFBgcICQoLDA0ODw==";

    @Test
    void encryptsAndAuthenticatesCredentialValues() {
        CredentialCipher cipher = new CredentialCipher("test-secret", SALT);

        CredentialCipher.EncryptedValue encryptedUser = cipher.encrypt("Admin");
        CredentialCipher.EncryptedValue encryptedPassword = cipher.encrypt("Pwd");

        assertTrue(encryptedUser.ciphertext().startsWith("ENC("));
        assertNotEquals(encryptedUser.nonce(), encryptedPassword.nonce());
        assertEquals("Admin", cipher.decrypt(encryptedUser.ciphertext(), encryptedUser.nonce()));
        assertEquals("Pwd", cipher.decrypt(encryptedPassword.ciphertext(), encryptedPassword.nonce()));
        assertThrows(IllegalStateException.class,
                () -> new CredentialCipher("wrong-secret", SALT)
                        .decrypt(encryptedUser.ciphertext(), encryptedUser.nonce()));
    }

    @Test
    void loadsEncryptedDatabaseCredentialsFromYaml() {
        EncryptedDatabaseConfig config = EncryptedDatabaseConfig.load(
                "/db-config.yml", "FOUNDATION_DB_CONFIG_SECRET_EXAMPLE_ChangeMe");

        assertEquals("test-jdbc", config.databaseName());
        assertEquals("Admin", config.username());
        assertEquals("Pwd", config.password());
    }
}