package com.fmd.security_service.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Immutable error response object for API errors using Java record.
 * <p>
 * Contains timestamp, HTTP status, error, message, and request path.
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
public record ApiError(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path) {
    /**
     * Convenience constructor for building ApiError from HttpStatus, message, and
     * path.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @param path    the request path
     */
    public ApiError(HttpStatus status, String message, String path) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path);
    }
}
