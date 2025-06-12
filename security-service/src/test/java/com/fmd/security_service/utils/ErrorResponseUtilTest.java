package com.fmd.security_service.utils;

import com.fmd.security_service.dto.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import static java.lang.reflect.Modifier.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    private static final String EXPECTED_JSON = """
            {"timestamp":"2025-06-09 12:00:00","status":400,"error":"Bad Request","message":"Invalid input","path":"/api/test"}""";

    /**
     * Tests that ErrorResponseUtil is defined as a Final class.
     */
    @Test
    void testErrorResponseUtil_isFinal() {
        log.info("Testing ErrorResponseUtil is defined as a Final class");
        assertThat(ErrorResponseUtil.class)
                .isFinal();
    }

    /**
     * Tests that ErrorResponseUtil has a private constructor.
     * <p>
     * Verifies that the constructor is not accessible from outside the class.
     * </p>
     */
    @Test
    void testErrorResponse_hasPrivateConstructor() throws Exception {
        log.info("Testing ErrorResponseUtil has a private constructor");

        var declaredConstructor = ErrorResponseUtil.class.getDeclaredConstructor();
        assertThat(declaredConstructor.getModifiers())
                .as("Constructor should be private")
                .isEqualTo(PRIVATE);
        declaredConstructor.setAccessible(true);
        assertThatThrownBy(declaredConstructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);

    }

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
        verify(response).setContentType(APPLICATION_JSON_VALUE);
        String json = stringWriter.toString();
        log.debug("JSON written to response: {}", json);
        assertThat(json).isEqualTo(EXPECTED_JSON);
    }
}
