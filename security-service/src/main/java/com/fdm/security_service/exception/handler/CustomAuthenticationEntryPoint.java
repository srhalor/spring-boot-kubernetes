package com.fdm.security_service.exception.handler;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fdm.security_service.dto.ApiError;
import com.fdm.security_service.utils.ErrorResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom AuthenticationEntryPoint to return a JSON response on authentication
 * failure.
 * <p>
 * This class implements Spring Security's AuthenticationEntryPoint interface
 * and is used to handle
 * authentication errors by returning a structured JSON response containing
 * error details.
 *
 * <p>
 * On authentication failure, it responds with HTTP 401 (Unauthorized) and a
 * JSON body
 * containing an {@link ApiError} object with the error message and request URI.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Handles authentication failures by sending a JSON response with error
     * details.
     *
     * @param request       the HttpServletRequest
     * @param response      the HttpServletResponse
     * @param authException the exception that caused the authentication failure
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(SC_UNAUTHORIZED);
        // Build ApiError object for the response
        ApiError apiError = new ApiError(UNAUTHORIZED,
                "Authentication failed: " + authException.getMessage(),
                request.getRequestURI());
        ErrorResponseUtil.writeErrorResponse(response, SC_UNAUTHORIZED, apiError);
    }
}
