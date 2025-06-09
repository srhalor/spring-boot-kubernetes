package com.fdm.security_service.exception.handler;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fdm.security_service.dto.ApiError;
import com.fdm.security_service.utils.ErrorResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom AccessDeniedHandler to return a JSON response on access denied.
 * <p>
 * This class implements Spring Security's AccessDeniedHandler interface and is
 * used to handle
 * authorization errors by returning a structured JSON response containing error
 * details.
 *
 * <p>
 * On access denied, it responds with HTTP 403 (Forbidden) and a JSON body
 * containing an {@link ApiError} object with the error message and request URI.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles access denied errors by sending a JSON response with error details.
     *
     * @param request               the HttpServletRequest
     * @param response              the HttpServletResponse
     * @param accessDeniedException the exception that caused the access denial
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        ApiError apiError = new ApiError(FORBIDDEN,
                "Access Denied: " + accessDeniedException.getMessage(),
                request.getRequestURI());
        ErrorResponseUtil.writeErrorResponse(response, SC_FORBIDDEN, apiError);
    }
}
