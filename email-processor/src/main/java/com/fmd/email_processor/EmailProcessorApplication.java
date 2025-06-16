package com.fmd.email_processor;

import com.fmd.email_processor.dto.BatchJobProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main application class for the Email Processor Service.
 * <p>
 * This class serves as the entry point for the Spring Boot application,
 * enabling the email processing functionalities.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(BatchJobProperties.class)
public class EmailProcessorApplication {

	/**
	 * Main method to run the Email Processor application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		log.info("Starting Email Processor Application...");
		SpringApplication.run(EmailProcessorApplication.class, args);
		log.info("Email Processor Application started successfully.");
	}
}
