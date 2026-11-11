package com.fp.boot_demo_web.integrated;

import org.springframework.boot.SpringApplication;

import com.fp.boot_demo_web.BootDemoWebApplication;

/** Development entry point for running the application with test PostgreSQL. */
class TestBasicPostgresApplication {

    /** Starts the application with the Testcontainers PostgreSQL configuration. */
    public static void main(String[] args) {
        SpringApplication.from(BootDemoWebApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
