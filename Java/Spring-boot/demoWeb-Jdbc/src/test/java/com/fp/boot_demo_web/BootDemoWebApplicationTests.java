package com.fp.boot_demo_web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Smoke tests for the application context under the test profile. */
@SpringBootTest
@ActiveProfiles("test")
class BootDemoWebApplicationTests {

	@Test
	void contextLoads() {
	}

}
