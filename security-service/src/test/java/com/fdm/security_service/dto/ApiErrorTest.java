package com.fdm.security_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for {@link ApiError} record.
 * <p>
 * Verifies correct construction, field values, and immutability.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
class ApiErrorTest {

    /**
     * Tests that the default constructor initializes fields correctly.
     */
    @Test
    void testAllArgsConstructor_setsAllFields() {
        log.info("Testing all-args constructor for ApiError");
        LocalDateTime now = LocalDateTime.of(2025, 6, 9, 12, 0, 0);
        int status = 404;
        String error = "Not Found";
        String message = "Resource not found";
        String path = "/api/test";
        ApiError apiError = new ApiError(now, status, error, message, path);
        log.debug("ApiError created: {}", apiError);
        assertEquals(now, apiError.timestamp());
        assertEquals(status, apiError.status());
        assertEquals(error, apiError.error());
        assertEquals(message, apiError.message());
        assertEquals(path, apiError.path());
    }

    /**
     * Tests that the convenience constructor initializes fields from HttpStatus.
     * 
     * @see ApiError#ApiError(HttpStatus, String, String)
     */
    @Test
    void testConvenienceConstructor_setsFieldsFromHttpStatus() {
        log.info("Testing convenience constructor for ApiError with HttpStatus");
        String message = "Unauthorized access";
        String path = "/api/secure";
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, message, path);
        log.debug("ApiError created: {}", apiError);
        assertNotNull(apiError.timestamp());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), apiError.status());
        assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), apiError.error());
        assertEquals(message, apiError.message());
        assertEquals(path, apiError.path());
    }

    /**
     * Tests that the convenience constructor sets the timestamp to the current
     * time.
     * 
     * @see ApiError#ApiError(HttpStatus, String, String)
     */
    @Test
    void testImmutability() {
        log.info("Testing immutability of ApiError record");
        new ApiError(HttpStatus.BAD_REQUEST, "Bad request", "/api/bad");
        // Record fields are final; no setters exist
        assertThrows(NoSuchMethodException.class,
                () -> ApiError.class.getDeclaredMethod("setMessage", String.class));
    }
}
