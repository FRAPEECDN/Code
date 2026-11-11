package com.frapee.securitydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstraps the Spring Boot security demo application.
 * <p>
 * The application demonstrates how Spring Security can protect access to
 * different endpoint groups using roles and authorities after a password and
 * time-based one-time password (TOTP) challenge.
 */
@SpringBootApplication
public class SecuritydemoApplication {

	/**
	 * Starts the application and initializes the embedded web server.
	 *
	 * @param args command-line arguments passed to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(SecuritydemoApplication.class, args);
	}

}
