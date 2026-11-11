package com.fp.boot_demo_web.integrated;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mongodb.MongoDBContainer;

/** Provides the MongoDB service used by integration tests. */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    /** Starts MongoDB 8 as a single-node replica set. */
    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer("mongo:8.0").withReplicaSet();
    }
}
