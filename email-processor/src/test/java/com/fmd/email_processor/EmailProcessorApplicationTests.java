package com.fmd.email_processor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the EmailProcessorApplication class.
 * <p>
 * This class verifies that the Spring application context loads successfully.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class EmailProcessorApplicationTests {

	/**
	 * Tests that the application context loads without any issues.
	 * <p>
	 * This test is intentionally left empty to verify that the Spring application
	 * context loads successfully.
	 * </p>
	 */
	@Test
	void contextLoads() {
		log.info("Verifying that the Spring application context loads successfully");
		// This test is intentionally left empty to verify that the Spring application
		// context loads successfully.
	}

}
