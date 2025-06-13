package com.fmd.security_service.dto;

import com.fmd.security_service.testutil.ApiErrorAssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        log.debug("ApiError created : {}", apiError);
        ApiErrorAssertUtil.assertApiErrorFields(apiError, now, status, error, message, path);
    }

    /**
     * Tests that the convenience constructor initializes fields from HttpStatus and handles null/empty message/path.
     */
    @Test
    void testConvenienceConstructor_variousCases() {
        log.info("Testing convenience constructor for ApiError with HttpStatus and null/empty message/path");
        // Normal
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Unauthorized access", "/api/secure");
        assertThat(apiError.timestamp()).isNotNull();
        assertThat(apiError.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(apiError.error()).isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        assertThat(apiError.message()).isEqualTo("Unauthorized access");
        assertThat(apiError.path()).isEqualTo("/api/secure");
        // Null message
        ApiError nullMsg = new ApiError(HttpStatus.BAD_REQUEST, null, "/api/nullmsg");
        assertThat(nullMsg.message()).isNull();
        assertThat(nullMsg.path()).isEqualTo("/api/nullmsg");
        // Null path
        ApiError nullPath = new ApiError(HttpStatus.BAD_REQUEST, "msg", null);
        assertThat(nullPath.message()).isEqualTo("msg");
        assertThat(nullPath.path()).isNull();
        // Empty message
        ApiError emptyMsg = new ApiError(HttpStatus.BAD_REQUEST, "", "/api/emptymsg");
        assertThat(emptyMsg.message()).isEmpty();
        assertThat(emptyMsg.path()).isEqualTo("/api/emptymsg");
        // Empty path
        ApiError emptyPath = new ApiError(HttpStatus.BAD_REQUEST, "msg", "");
        assertThat(emptyPath.message()).isEqualTo("msg");
        assertThat(emptyPath.path()).isEmpty();
    }

    /**
     * Tests that the record is immutable (no setters).
     */
    @Test
    void testImmutability() {
        log.info("Testing immutability of ApiError record");
        new ApiError(HttpStatus.BAD_REQUEST, "Bad request", "/api/bad");
        // Record fields are final; no setters exist
        //noinspection JavaReflectionMemberAccess
        assertThatThrownBy(() -> ApiError.class.getDeclaredMethod("setMessage", String.class))
                .isInstanceOf(NoSuchMethodException.class);
    }
}
