package com.fmd.security_service.exception.handler;

import com.fmd.security_service.dto.ApiError;
import com.fmd.security_service.utils.ErrorResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

        // Build ApiError object for the response
        ApiError apiError = new ApiError(UNAUTHORIZED,
                "Authentication failed: " + authException.getMessage(),
                request.getRequestURI());
        ErrorResponseUtil.writeErrorResponse(response, SC_UNAUTHORIZED, apiError);
    }
}
