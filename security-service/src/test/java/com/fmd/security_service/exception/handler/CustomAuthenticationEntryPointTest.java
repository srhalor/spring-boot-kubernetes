package com.fmd.security_service.exception.handler;

import com.fmd.security_service.dto.ApiError;
import com.fmd.security_service.utils.ErrorResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CustomAuthenticationEntryPoint}.
 * <p>
 * Verifies that a 401 response with an ApiError is sent on authentication
 * failure.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
class CustomAuthenticationEntryPointTest {
    private final CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final AuthenticationException exception = mock(AuthenticationException.class);

    /**
     * Sets up the test environment before each test.
     * Initializes the CustomAuthenticationEntryPoint and mocks for
     * HttpServletRequest, HttpServletResponse, and AuthenticationException.
     */
    @BeforeEach
    void setUp() {
        log.info("Setting up test environment for CustomAuthenticationEntryPointTest");
        when(exception.getMessage()).thenReturn("Invalid credentials");
    }

    /**
     * Tests that commence() sends a 401 response with an ApiError.
     * <p>
     * Verifies that the ErrorResponseUtil.writeErrorResponse method is called
     * with the correct parameters.
     * </p>
     *
     * @throws IOException if an input or output exception occurs
     */
    @Test
    void commence_sendsApiErrorWith401() throws IOException {
        log.info("Testing that commence() sends a 401 response with ApiError");
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/test");
        try (var util = mockStatic(ErrorResponseUtil.class)) {
            // Act
            entryPoint.commence(request, response, exception);

            // Assert
            util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(401), any(ApiError.class)));
            log.debug("Verified ErrorResponseUtil.writeErrorResponse called with 401 and ApiError");
        }
    }

    /**
     * Tests that commence() sets the ApiError fields correctly.
     * <p>
     * Verifies that the ApiError object contains the expected status, error,
     * message, path, and timestamp.
     * </p>
     *
     * @throws IOException if an input or output exception occurs
     */
    @Test
    void commence_setsApiErrorFieldsCorrectly() throws IOException {
        log.info("Testing that commence() sets ApiError fields correctly");
        when(request.getRequestURI()).thenReturn("/api/test");
        try(var util = mockStatic(ErrorResponseUtil.class)) {
            ArgumentCaptor<ApiError> captor = ArgumentCaptor.forClass(ApiError.class);

            entryPoint.commence(request, response, exception);

            util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(401), captor.capture()));
            ApiError apiError = captor.getValue();
            log.debug("ApiError captured: {}", apiError);
            assertThat(apiError.status()).isEqualTo(401);
            assertThat(apiError.error()).isEqualTo("Unauthorized");
            assertThat(apiError.message()).contains("Invalid credentials");
            assertThat(apiError.path()).isEqualTo("/api/test");
            assertThat(apiError.timestamp()).isNotNull();
        }
    }

}
