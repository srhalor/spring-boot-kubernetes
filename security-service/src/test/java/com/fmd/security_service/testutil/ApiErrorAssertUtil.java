package com.fmd.security_service.testutil;

import com.fmd.security_service.dto.ApiError;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;

/**
 * Utility class for asserting ApiError objects in tests.
 * <p>
 * Provides methods to verify that ApiError objects have the expected fields
 * and values, ensuring consistent error handling in the security service.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@UtilityClass
public final class ApiErrorAssertUtil {

    /**
     * Asserts that the ApiError object has the expected fields.
     *
     * @param apiError  the ApiError object to assert
     * @param timestamp the expected timestamp
     * @param status    the expected HTTP status code
     * @param error     the expected error type
     * @param message   the expected error message
     * @param path      the expected request path
     */
    public static void assertApiErrorFields(ApiError apiError, LocalDateTime timestamp, int status, String error, String message, String path) {
        Assertions.assertThat(apiError.timestamp()).isEqualTo(timestamp);
        assertApiError(apiError, status, error, message, path);
    }

    /**
     * Asserts that the ApiError object has the expected fields.
     *
     * @param apiError        the ApiError object to assert
     * @param status          the expected HTTP status code
     * @param error           the expected error type
     * @param messageContains a part of the expected error message
     * @param path            the expected request path
     */
    public static void assertApiError(ApiError apiError, int status, String error, String messageContains, String path) {
        Assertions.assertThat(apiError.status()).isEqualTo(status);
        Assertions.assertThat(apiError.error()).isEqualTo(error);
        Assertions.assertThat(apiError.message()).contains(messageContains);
        Assertions.assertThat(apiError.path()).isEqualTo(path);
        Assertions.assertThat(apiError.timestamp()).isNotNull();
    }
}
