package com.fdm.security_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Main application class for the Security Service.
 * <p>
 * This class serves as the entry point for the Spring Boot application,
 * enabling the security service functionalities.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@SpringBootApplication
public class SecurityServiceApplication {

	/**
	 * Main method to run the Security Service application.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		log.info("Starting Security Service Application...");
		SpringApplication.run(SecurityServiceApplication.class, args);
		log.info("Security Service Application started successfully.");
	}

}
