package com.frapee.JwtSecurityDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for the JWT security demo application.
 *
 * <p>This Spring Boot entry point starts the application with the security,
 * JWT, and controller configuration defined in the project.</p>
 */
@SpringBootApplication
public class JwtSecurityDemoApplication {

	/**
	 * Creates the Spring Boot application entry point.
	 */
	public JwtSecurityDemoApplication() {
	}

	/**
	 * Starts the embedded Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(JwtSecurityDemoApplication.class, args);
	}

}
