package com.fp.boot_demo_web.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the Jasypt encryptor used to decrypt encrypted application
 * properties.
 */
@Configuration
@ConditionalOnProperty(prefix = "jasypt.encryptor", name = "password")
public class EncryptionConfig {

    /**
     * Creates the application encryptor using the external Jasypt key.
     *
     * @return the configured encryptor
     * @throws IllegalStateException if {@code JASYPT_ENCRYPTOR_PASSWORD} is absent
     */
    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor jasyptStringEncryptor() {
        String password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "JASYPT_ENCRYPTOR_PASSWORD environment variable is not configured");
        }

        return createEncryptor(password);
    }

    /**
     * Creates the encryptor shared by application configuration and the
     * credential-launch utility.
     *
     * @param password the external encryption key
     * @return an AES-256 PBE string encryptor
     */
    static StandardPBEStringEncryptor createEncryptor(String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setKeyObtentionIterations(10000);
        encryptor.setProviderName("SunJCE");
        encryptor.setSaltGenerator(new org.jasypt.salt.RandomSaltGenerator());
        encryptor.setIvGenerator(new RandomIvGenerator());

        return encryptor;
    }
}
