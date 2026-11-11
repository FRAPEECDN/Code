package com.fp.boot_demo_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point and auto-configuration root for the Demo Web JPA Full application.
 */
@SpringBootApplication
public class BootDemoWebApplication {

	/**
	 * Starts the Spring application with the supplied command-line arguments.
	 *
	 * @param args command-line arguments forwarded to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(BootDemoWebApplication.class, args);
	}

}
