package com.fp.boot_demo_web.integrated;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/** Provides the PostgreSQL service used by integration-test application contexts. */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    /** Starts a PostgreSQL 17 container and supplies its connection details to Boot. */
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:17-alpine");
    }
}
