package com.fdm.security_service.controller;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for health check endpoint.
 * <p>
 * This controller exposes a health check endpoint that returns the health
 * status
 * of the application using Spring Boot Actuator's HealthEndpoint.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    /**
     * Health check endpoint that returns the health status of the application.
     * <p>
     * This endpoint uses Spring Boot Actuator's HealthEndpoint to retrieve the
     * health status and returns it as a string.
     * </p>
     *
     * @return the health status of the application
     */
    @GetMapping
    public String healthCheck() {
        // Use Spring Boot Actuator's HealthEndpoint to get health status
        var healthComponent = healthEndpoint.health();
        log.info("Health check endpoint accessed: {}", healthComponent.getStatus());
        return healthComponent.getStatus().toString();
    }

}
