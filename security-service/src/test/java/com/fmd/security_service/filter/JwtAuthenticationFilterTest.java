package com.fmd.security_service.filter;

import jakarta.servlet.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JwtAuthenticationFilter}.
 * <p>
 * This class tests the JWT authentication filter's behavior with various
 * scenarios:
 * <ul>
 * <li>Valid JWT token</li>
 * <li>Missing Authorization header</li>
 * <li>Invalid JWT token</li>
 * <li>Expired JWT token</li>
 * </ul>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
class JwtAuthenticationFilterTest {
    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc" +
            "3MiOiJUZXN0IiwiaWF0IjoxNzQ3OTI3NjkyLCJleHAiOjE3Nzk0NjM2OTcsImF1ZCI6" +
            "ImF1ZGlhbmNlIiwic3ViIjoidXNlcm5hbWUiLCJuYW1lIjoiSm9obm55Iiwicm9sZXM" +
            "iOlsiVXNlciIsIkFkbWluIl19.2gS8pFXJuQ1u19GjDp7UcyTRVVlmBWNnJSukjDz2ENQ";

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that a valid JWT token sets authentication in the security context.
     */
    @Test
    void testDoFilterInternal_validToken_setsAuthentication() throws Exception {
        log.info("Testing valid JWT token sets authentication");
        request.addHeader("Authorization", VALID_TOKEN);
        filter.doFilterInternal(request, response, filterChain);
        log.debug("Authentication after filter: {}", SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be set");
        assertEquals("username", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests that a valid JWT token but authentication is already set
     * in the security context.
     */
    @Test
    void testDoFilterInternal_validToken_alreadyAuthenticated() throws Exception {
        log.info("Testing valid JWT token with already authenticated context");
        SecurityContextHolder.getContext().setAuthentication(mock(org.springframework.security.core.Authentication.class));
        request.addHeader("Authorization", VALID_TOKEN);
        filter.doFilterInternal(request, response, filterChain);
        log.debug("Authentication after filter: {}", SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should still be set");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests that a missing Authorization header does not set authentication.
     */
    @Test
    void testDoFilterInternal_missingAuthorizationHeader() throws Exception {
        log.info("Testing missing Authorization header");
        filter.doFilterInternal(request, response, filterChain);
        log.debug("Authentication after filter: {}", SecurityContextHolder.getContext().getAuthentication());
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests that an invalid JWT token does not set authentication.
     */
    @Test
    void testDoFilterInternal_invalidToken() throws Exception {
        log.info("Testing invalid JWT token");
        request.addHeader("Authorization", "Bearer invalid.token.value");
        filter.doFilterInternal(request, response, filterChain);
        log.debug("Authentication after filter: {}", SecurityContextHolder.getContext().getAuthentication());
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests that an expired JWT token does not set authentication.
     */
    @Test
    void testDoFilterInternal_expiredToken() throws Exception {
        log.info("Testing expired JWT token");
        String expiredToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjEwMDAwMDAwMDB9.sig";
        request.addHeader("Authorization", expiredToken);
        filter.doFilterInternal(request, response, filterChain);
        log.debug("Authentication after filter: {}", SecurityContextHolder.getContext().getAuthentication());
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
