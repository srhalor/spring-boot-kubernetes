package com.fmd.order_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for the {@link HealthController} class.
 * <p>
 * These tests verify the behavior of the health check endpoint exposed by the
 * controller.
 * The controller is tested in isolation using a mocked {@link HealthEndpoint}.
 * </p>
 *
 * <ul>
 * <li>
 * <b>healthCheck_shouldReturnUpStatus:</b>
 * Ensures that when the health endpoint reports an "UP" status, the controller
 * returns "UP" with HTTP 200.
 * </li>
 * <li>
 * <b>healthCheck_shouldReturnDownStatus:</b>
 * Ensures that when the health endpoint reports a "DOWN" status, the controller
 * returns "DOWN" with HTTP 200.
 * </li>
 * </ul>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 * @see HealthController
 */
@Slf4j
class HealthControllerTest {

    /**
     * Tests the health check endpoint when the health status is "UP".
     * <p>
     * This test mocks the {@link HealthEndpoint} to return an "UP" status and
     * verifies that the controller
     * responds with "UP" and HTTP status 200.
     * </p>
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void healthCheck_shouldReturnUpStatus() throws Exception {
        log.info("Starting test: healthCheck_shouldReturnUpStatus");
        HealthEndpoint mockHealthEndpoint = Mockito.mock(HealthEndpoint.class);
        when(mockHealthEndpoint.health()).thenReturn(Health.up().build());

        HealthController controller = new HealthController(mockHealthEndpoint);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        log.debug("Performing GET /health request");
        mockMvc.perform(get("/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
        log.info("Test healthCheck_shouldReturnUpStatus completed successfully");
    }

    /**
     * Tests the health check endpoint when the health status is "DOWN".
     * <p>
     * This test mocks the {@link HealthEndpoint} to return a "DOWN" status and
     * verifies that the controller
     * responds with "DOWN" and HTTP status 200.
     * </p>
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void healthCheck_shouldReturnDownStatus() throws Exception {
        log.info("Starting test: healthCheck_shouldReturnDownStatus");
        HealthEndpoint mockHealthEndpoint = Mockito.mock(HealthEndpoint.class);
        when(mockHealthEndpoint.health()).thenReturn(Health.down().build());

        HealthController controller = new HealthController(mockHealthEndpoint);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        log.debug("Performing GET /health request");
        mockMvc.perform(get("/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("DOWN"));
        log.info("Test healthCheck_shouldReturnDownStatus completed successfully");
    }
}
