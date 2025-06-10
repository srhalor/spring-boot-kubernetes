package com.fmd.order_service;

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
public class OrderServiceApplication {
	/**
	 * Main method to run the Security Service application.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		log.info("Starting Order Service Application...");
		SpringApplication.run(OrderServiceApplication.class, args);
		log.info("Order Service Application started successfully.");
	}

}
