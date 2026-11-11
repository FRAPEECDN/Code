package com.fp.boot_demo_web.integrated;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** Verifies that the application context starts against PostgreSQL. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class BootDemoWebApplicationIT {

    @Test
    void contextLoadsWithPostgres() {
    }
}
