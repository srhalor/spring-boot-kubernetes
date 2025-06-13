package com.fmd.security_service.exception.handler;

import com.fmd.security_service.dto.ApiError;
import com.fmd.security_service.testutil.ApiErrorAssertUtil;
import com.fmd.security_service.utils.ErrorResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
        ApiErrorAssertUtil.assertApiError(apiError, 403, "Forbidden", "Forbidden for test", "/api/test");
        util.close();
    }

    /**
     * Tests handle() when exception message and request URI are null or empty.
     * Ensures ApiError is constructed and no exceptions are thrown.
     */
    @Test
    void handle_withNullOrEmptyMessageAndUri() throws IOException {
        log.info("Testing handle() with null/empty message and URI");
        // Case 1: null message
        AccessDeniedException nullMsgEx = new AccessDeniedException(null);
        when(request.getRequestURI()).thenReturn("/api/nullmsg");
        var util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, nullMsgEx);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();

        // Case 2: empty message
        AccessDeniedException emptyMsgEx = new AccessDeniedException("");
        when(request.getRequestURI()).thenReturn("/api/emptymessage");
        util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, emptyMsgEx);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();

        // Case 3: null URI
        AccessDeniedException ex = new AccessDeniedException("Forbidden for test");
        when(request.getRequestURI()).thenReturn(null);
        util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, ex);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();

        // Case 4: empty URI
        when(request.getRequestURI()).thenReturn("");
        util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, ex);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();
    }

    /**
     * Tests handle() when both exception message and request URI are null or empty.
     * Ensures ApiError is constructed and no exceptions are thrown.
     */
    @Test
    void handle_withBothMessageAndUriNullOrEmpty() throws IOException {
        log.info("Testing handle() with both message and URI null or empty");
        // Both null
        AccessDeniedException nullMsgEx = new AccessDeniedException(null);
        when(request.getRequestURI()).thenReturn(null);
        var util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, nullMsgEx);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();

        // Both empty
        AccessDeniedException emptyMsgEx = new AccessDeniedException("");
        when(request.getRequestURI()).thenReturn("");
        util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, emptyMsgEx);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();
    }

    /**
     * Tests handle() with all combinations of null/non-null for
     * accessDeniedException.getMessage() and request.getRequestURI().
     * Ensures ApiError is constructed correctly in all cases.
     */
    @Test
    void handle_allMessageAndUriCombinations() throws IOException {
        log.info("Testing handle() with all combinations of null/non-null message and URI");
        // 1. getMessage() == null, getRequestURI() == null
        AccessDeniedException exNullMsg = new AccessDeniedException(null);
        when(request.getRequestURI()).thenReturn(null);
        var util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, exNullMsg);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();

        // 2. getMessage() == null, getRequestURI() != null
        when(request.getRequestURI()).thenReturn("/api/nullmsg");
        util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, exNullMsg);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();

        // 3. getMessage() != null, getRequestURI() == null
        AccessDeniedException exMsg = new AccessDeniedException("Forbidden for test");
        when(request.getRequestURI()).thenReturn(null);
        util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, exMsg);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();

        // 4. getMessage() != null, getRequestURI() != null
        when(request.getRequestURI()).thenReturn("/api/test");
        util = mockStatic(ErrorResponseUtil.class);
        handler.handle(request, response, exMsg);
        util.verify(() -> ErrorResponseUtil.writeErrorResponse(eq(response), eq(403), any(ApiError.class)));
        util.close();
    }
}
