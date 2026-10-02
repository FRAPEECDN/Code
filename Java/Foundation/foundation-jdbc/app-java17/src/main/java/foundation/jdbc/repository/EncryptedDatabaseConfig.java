package foundation.jdbc.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import org.yaml.snakeyaml.Yaml;

/**
 * Database connection settings loaded from YAML with encrypted credentials.
 *
 * @param databaseName application database to create/use
 * @param adminJdbcUrl URL for an existing maintenance database
 * @param jdbcUrl URL for the application database
 * @param username decrypted database user
 * @param password decrypted database password
 */
public record EncryptedDatabaseConfig(
        String databaseName,
        String adminJdbcUrl,
        String jdbcUrl,
        String username,
        String password) {

    /**
     * Loads the {@code db} mapping from a classpath resource and decrypts its username and password.
     *
     * @param resourceName absolute classpath resource name, for example {@code /db-config.yml}
     * @param secret passphrase used to derive the AES key
     * @return validated database connection settings
     * @throws IllegalStateException if the resource, required values, or decryption is invalid
     */
    public static EncryptedDatabaseConfig load(String resourceName, String secret) {
        Objects.requireNonNull(resourceName, "resourceName");
        Objects.requireNonNull(secret, "secret");
        try (InputStream stream = EncryptedDatabaseConfig.class.getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new IllegalStateException("Missing database config resource: " + resourceName);
            }
            Map<String, Object> root = new Yaml().load(stream);
            if (root == null || !(root.get("db") instanceof Map<?, ?> database)) {
                throw new IllegalStateException("Database config must contain a db mapping");
            }
            String salt = value(database, "salt");
            CredentialCipher cipher = new CredentialCipher(secret, salt);
            return new EncryptedDatabaseConfig(
                    value(database, "databaseName"),
                    value(database, "adminUrl"),
                    value(database, "url"),
                    cipher.decrypt(value(database, "username"), value(database, "usernameNonce")),
                    cipher.decrypt(value(database, "password"), value(database, "passwordNonce")));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load database config resource " + resourceName, exception);
        }
    }

    private static String value(Map<?, ?> config, String key) {
        Object value = config.get(key);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalStateException("Missing database config value: " + key);
        }
        return text;
    }
}