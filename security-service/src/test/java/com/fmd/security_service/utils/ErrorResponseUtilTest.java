package com.fmd.security_service.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.fmd.security_service.dto.ApiError;
import com.fmd.security_service.utils.ErrorResponseUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for {@link ErrorResponseUtil}.
 * <p>
 * Verifies that ApiError is written as JSON with correct status and content
 * type.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
class ErrorResponseUtilTest {

    /**
     * Tests that writeErrorResponse() writes ApiError as JSON and sets the
     * response status and content type.
     * <p>
     * Verifies that the JSON contains all expected fields and values.
     * </p>
     *
     * @throws Exception if an error occurs during response writing
     */
    @Test
    void writeErrorResponse_writesJsonAndSetsStatusAndContentType() throws Exception {
        log.info("Testing writeErrorResponse writes JSON and sets status/content type");
        // Arrange
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        ApiError apiError = new ApiError(LocalDateTime.of(2025, 6, 9, 12, 0, 0), 400, "Bad Request", "Invalid input",
                "/api/test");
        log.debug("ApiError to write: {}", apiError);

        // Act
        ErrorResponseUtil.writeErrorResponse(response, 400, apiError);
        printWriter.flush();

        // Assert
        verify(response).setStatus(400);
        verify(response).setContentType("application/json");
        String json = stringWriter.toString();
        log.debug("JSON written to response: {}", json);
        assertTrue(json.contains("\"status\":400"));
        assertTrue(json.contains("\"error\":\"Bad Request\""));
        assertTrue(json.contains("\"message\":\"Invalid input\""));
        assertTrue(json.contains("\"path\":\"/api/test\""));
        assertTrue(json.contains("\"timestamp\":\"2025-06-09 12:00:00\""));
    }
}
