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
        assertThat(apiError.timestamp()).isNotNull();
        assertThat(apiError.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(apiError.error()).isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        assertThat(apiError.message()).isEqualTo(message);
        assertThat(apiError.path()).isEqualTo(path);
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
        //noinspection JavaReflectionMemberAccess
        assertThatThrownBy(() -> ApiError.class.getDeclaredMethod("setMessage", String.class))
                .isInstanceOf(NoSuchMethodException.class);
    }

    /**
     * Tests convenience constructor with null and empty message/path.
     */
    @Test
    void testConvenienceConstructor_withNullAndEmptyMessageAndPath() {
        log.info("Testing convenience constructor with null/empty message and path");
        // Null message
        ApiError nullMsg = new ApiError(HttpStatus.BAD_REQUEST, null, "/api/nullmsg");
        assertThat(nullMsg.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(nullMsg.error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(nullMsg.message()).isNull();
        assertThat(nullMsg.path()).isEqualTo("/api/nullmsg");
        assertThat(nullMsg.timestamp()).isNotNull();

        // Null path
        ApiError nullPath = new ApiError(HttpStatus.BAD_REQUEST, "msg", null);
        assertThat(nullPath.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(nullPath.error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(nullPath.message()).isEqualTo("msg");
        assertThat(nullPath.path()).isNull();
        assertThat(nullPath.timestamp()).isNotNull();

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
     * Tests all-args constructor with null and empty message/path.
     */
    @Test
    void testAllArgsConstructor_withNullAndEmptyMessageAndPath() {
        log.info("Testing all-args constructor with null/empty message and path");
        LocalDateTime now = LocalDateTime.now();
        // Null message
        ApiError nullMsg = new ApiError(now, 400, "Bad Request", null, "/api/nullmsg");
        assertThat(nullMsg.message()).isNull();
        assertThat(nullMsg.path()).isEqualTo("/api/nullmsg");
        // Null path
        ApiError nullPath = new ApiError(now, 400, "Bad Request", "msg", null);
        assertThat(nullPath.message()).isEqualTo("msg");
        assertThat(nullPath.path()).isNull();
        // Empty message
        ApiError emptyMsg = new ApiError(now, 400, "Bad Request", "", "/api/emptymsg");
        assertThat(emptyMsg.message()).isEmpty();
        assertThat(emptyMsg.path()).isEqualTo("/api/emptymsg");
        // Empty path
        ApiError emptyPath = new ApiError(now, 400, "Bad Request", "msg", "");
        assertThat(emptyPath.message()).isEqualTo("msg");
        assertThat(emptyPath.path()).isEmpty();
    }
}
