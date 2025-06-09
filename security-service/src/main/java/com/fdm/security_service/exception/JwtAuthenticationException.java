package com.fdm.security_service.exception;

import lombok.experimental.StandardException;

/**
 * Exception thrown when JWT payload validation fails (e.g., missing subject,
 * expiration, or expired token).
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@StandardException
public class JwtAuthenticationException extends RuntimeException {
}
