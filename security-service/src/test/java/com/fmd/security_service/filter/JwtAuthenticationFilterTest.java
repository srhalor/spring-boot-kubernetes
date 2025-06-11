package com.fmd.security_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
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
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final FilterChain filterChain = mock(FilterChain.class);

    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc" +
            "3MiOiJUZXN0IiwiaWF0IjoxNzQ3OTI3NjkyLCJleHAiOjE3Nzk0NjM2OTcsImF1ZCI6" +
            "ImF1ZGlhbmNlIiwic3ViIjoidXNlcm5hbWUiLCJuYW1lIjoiSm9obm55Iiwicm9sZXM" +
            "iOlsiVXNlciIsIkFkbWluIl19.2gS8pFXJuQ1u19GjDp7UcyTRVVlmBWNnJSukjDz2ENQ";

    @BeforeEach
    void setUp() {
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
        doFilter(VALID_TOKEN);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull()
                .extracting(Principal::getName)
                .isEqualTo("username");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests that a valid JWT token sets details in the authentication object.
     */
    @Test
    void testDoFilterInternal_validToken_setsDetails() throws Exception {
        log.info("Testing valid JWT token sets details in authentication");
        doFilter(VALID_TOKEN);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getDetails())
                .isNotNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }


    /**
     * Tests that a valid JWT token but authentication is already set
     * in the security context.
     */
    @Test
    void testDoFilterInternal_validToken_alreadyAuthenticated() throws Exception {
        log.info("Testing valid JWT token with already authenticated context");
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        doFilter(VALID_TOKEN);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests that a missing Authorization header does not set authentication.
     */
    @Test
    void testDoFilterInternal_missingAuthorizationHeader() throws Exception {
        log.info("Testing missing Authorization header");
        testAuthenticationIsNull(null);
    }

    /**
     * Tests that an invalid JWT token does not set authentication.
     */
    @Test
    void testDoFilterInternal_invalidToken() throws Exception {
        log.info("Testing invalid JWT token");
        testAuthenticationIsNull("Bearer invalid.token.value");
    }

    /**
     * Tests that an expired JWT token does not set authentication.
     */
    @Test
    void testDoFilterInternal_expiredToken() throws Exception {
        log.info("Testing expired JWT token");
        testAuthenticationIsNull("Bearer eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjEwMDAwMDAwMDB9.sig");
    }

    /**
     * Helper method to test that authentication is null after filtering.
     *
     * @param expiredToken the JWT token to test
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void testAuthenticationIsNull(String expiredToken) throws ServletException, IOException {
        doFilter(expiredToken);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Executes the filter with the provided JWT token.
     *
     * @param expiredToken the JWT token to test
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void doFilter(String expiredToken) throws ServletException, IOException {
        if (null != expiredToken) {
            request.addHeader("Authorization", expiredToken);
        }
        filter.doFilterInternal(request, response, filterChain);
        log.debug("Authentication after filter: {}", SecurityContextHolder.getContext().getAuthentication());
    }
}
