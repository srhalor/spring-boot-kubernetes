package com.fdm.security_service.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import lombok.extern.slf4j.Slf4j;

/**
 * Integration tests for {@link SecurityController}.
 * <p>
 * These tests verify the authentication and authorization behavior of the
 * /api/security/authenticate endpoint
 * using real JWT tokens and the application's security configuration.
 * </p>
 * <ul>
 * <li>200 OK for valid token with correct roles</li>
 * <li>403 Forbidden for valid token with incorrect/missing roles</li>
 * <li>401 Unauthorized for invalid token or missing Authorization header</li>
 * </ul>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class SecurityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private static final String URL = "/api/security/authenticate";
    private static final String VALID_TOKEN_CORRECT_ROLES = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUZXN0IiwiaWF0IjoxNzQ3OTI3NjkyLCJleHAiOjE3Nzk0NjM2OTcsImF1ZCI6ImF1ZGlhbmNlIiwic3ViIjoidXNlcm5hbWUiLCJuYW1lIjoiSm9obm55Iiwicm9sZXMiOlsiVXNlciIsIkFkbWluIl19.2gS8pFXJuQ1u19GjDp7UcyTRVVlmBWNnJSukjDz2ENQ";
    private static final String VALID_TOKEN_INCORRECT_ROLES = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUZXN0IiwiaWF0IjoxNzQ3OTI3NjkyLCJleHAiOjE3Nzk0NjM2OTcsImF1ZCI6ImF1ZGlhbmNlIiwic3ViIjoidXNlcm5hbWUiLCJuYW1lIjoiSm9obm55In0.z0kVJ8VRaoEg2xzMy9i2aJ-0sV1rfZLnVt2DuEycxbQ";
    private static final String INVALID_TOKEN = "Bearer invalid.token.value";

    /**
     * Test: Should return 200 OK for valid token with correct roles.
     *
     * <p>
     * Explanation:
     * </p>
     * <ul>
     * <li>Performs a POST request with a valid JWT containing 'User' and 'Admin'
     * roles.</li>
     * <li>Expects HTTP 200 and the correct success message.</li>
     * </ul>
     */
    @Test
    void whenValidTokenWithCorrectRoles_thenReturns200() throws Exception {
        log.info("Testing 200 OK for valid token with correct roles");
        // Perform POST with valid token and check for 200 OK
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                // Add Authorization header with valid token
                .header("Authorization", VALID_TOKEN_CORRECT_ROLES)
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP 200 OK
                .andExpect(status().isOk())
                // Expect correct response body
                .andExpect(MockMvcResultMatchers.content().string("User authenticated successfully"));
    }

    /**
     * Test: Should return 403 Forbidden for valid token with incorrect/missing
     * roles.
     *
     * <p>
     * Explanation:
     * </p>
     * <ul>
     * <li>Performs a POST request with a valid JWT missing required roles.</li>
     * <li>Expects HTTP 403 Forbidden.</li>
     * </ul>
     */
    @Test
    void whenValidTokenWithIncorrectRoles_thenReturns403() throws Exception {
        log.info("Testing 403 Forbidden for valid token with incorrect/missing roles");
        // Perform POST with valid token but missing required roles
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Authorization", VALID_TOKEN_INCORRECT_ROLES)
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP 403 Forbidden
                .andExpect(status().isForbidden());
    }

    /**
     * Test: Should return 401 Unauthorized for invalid token.
     *
     * <p>
     * Explanation:
     * </p>
     * <ul>
     * <li>Performs a POST request with an invalid JWT.</li>
     * <li>Expects HTTP 401 Unauthorized.</li>
     * </ul>
     */
    @Test
    void whenInvalidToken_thenReturns401() throws Exception {
        log.info("Testing 401 Unauthorized for invalid token");
        // Perform POST with invalid token
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Authorization", INVALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test: Should return 401 Unauthorized when Authorization header is missing.
     *
     * <p>
     * Explanation:
     * </p>
     * <ul>
     * <li>Performs a POST request without Authorization header.</li>
     * <li>Expects HTTP 401 Unauthorized.</li>
     * </ul>
     */
    @Test
    void whenNoAuthorizationHeader_thenReturns401() throws Exception {
        log.info("Testing 401 Unauthorized for missing Authorization header");
        // Perform POST without Authorization header
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }
}
