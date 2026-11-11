package com.fp.boot_demo_web.config;

/**
 * Command-line helper that emits Jasypt-wrapped database credentials for the
 * local launch scripts. Plaintext values are read from environment variables
 * and are never written to standard output.
 */
public final class DatabaseCredentialEncryptor {

    private DatabaseCredentialEncryptor() {
    }

    /**
     * Encrypts the database username and password from the process environment.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        String key = requiredEnvironmentVariable("JASYPT_ENCRYPTOR_PASSWORD");
        var encryptor = EncryptionConfig.createEncryptor(key);

        System.out.println("DB_USERNAME=ENC(" + encryptor.encrypt(requiredEnvironmentVariable("DB_USERNAME")) + ")");
        System.out.println("DB_PASSWORD=ENC(" + encryptor.encrypt(requiredEnvironmentVariable("DB_PASSWORD")) + ")");
    }

    /**
     * Reads a required, non-blank environment variable.
     *
     * @param name environment variable name
     * @return its value
     * @throws IllegalStateException if the variable is missing or blank
     */
    private static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " environment variable is required");
        }
        return value;
    }
}
