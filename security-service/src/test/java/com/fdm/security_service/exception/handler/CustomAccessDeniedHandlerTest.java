package com.fdm.security_service.exception.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import com.fdm.security_service.dto.ApiError;
import com.fdm.security_service.utils.ErrorResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for {@link CustomAccessDeniedHandler}.
 * <p>
 * Verifies that a 403 response with an ApiError is sent on access denied.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
class CustomAccessDeniedHandlerTest {
    private CustomAccessDeniedHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AccessDeniedException exception;

    /**
     * Sets up the test environment before each test.
     * Initializes the CustomAccessDeniedHandler and mocks for HttpServletRequest
     * and HttpServletResponse.
     */
    @BeforeEach
    void setUp() {
        log.info("Setting up test environment for CustomAccessDeniedHandlerTest");
        handler = new CustomAccessDeniedHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        exception = new AccessDeniedException("Forbidden for test");
    }

    /**
     * Tests that handle() sends a 403 response with an ApiError.
     * <p>
     * Verifies that the ErrorResponseUtil.writeErrorResponse method is called
     * with the correct parameters.
     * </p>
     *
     * @throws IOException if an input or output exception occurs
     */
    @Test
    void handle_sendsApiErrorWith403() throws IOException {
        log.info("Testing that handle() sends a 403 response with ApiError");
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/test");
        // Spy on ErrorResponseUtil to verify static method call
        var util = mockStatic(ErrorResponseUtil.class);

        // Act
        handler.handle(request, response, exception);

        // Assert
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        log.debug("Verified ErrorResponseUtil.writeErrorResponse called with 403 and ApiError");
        util.close();
    }

    /**
     * Tests that handle() sets the ApiError fields correctly.
     * <p>
     * Verifies that the ApiError object contains the expected status, error
     * message,
     * request path, and timestamp.
     * </p>
     *
     * @throws IOException if an input or output exception occurs
     */
    @Test
    void handle_setsApiErrorFieldsCorrectly() throws IOException {
        log.info("Testing that handle() sets ApiError fields correctly");
        when(request.getRequestURI()).thenReturn("/api/test");
        var util = mockStatic(ErrorResponseUtil.class);
        ArgumentCaptor<ApiError> captor = ArgumentCaptor.forClass(ApiError.class);

        handler.handle(request, response, exception);

        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), captor.capture()));
        ApiError apiError = captor.getValue();
        log.debug("ApiError captured: {}", apiError);
        assertEquals(403, apiError.status());
        assertEquals("Forbidden", apiError.error());
        assertTrue(apiError.message().contains("Forbidden for test"));
        assertEquals("/api/test", apiError.path());
        assertNotNull(apiError.timestamp());
        util.close();
    }
}
