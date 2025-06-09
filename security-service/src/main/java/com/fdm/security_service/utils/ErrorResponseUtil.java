package com.fdm.security_service.utils;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fdm.security_service.dto.ApiError;

import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

/**
 * Utility class for writing ApiError responses as JSON.
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@UtilityClass
public class ErrorResponseUtil {

    /**
     * Writes the given ApiError as a JSON response with the specified status code.
     *
     * @param response   the HttpServletResponse
     * @param statusCode the HTTP status code
     * @param apiError   the ApiError object to write
     * @throws IOException if an I/O error occurs
     */
    public static void writeErrorResponse(HttpServletResponse response,
            int statusCode,
            ApiError apiError) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(mapper.writeValueAsString(apiError));
    }
}
