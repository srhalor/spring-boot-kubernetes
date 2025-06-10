package com.fmd.security_service.exception;

import lombok.experimental.StandardException;

/**
 * Exception thrown when JWT payload parsing fails.
 * This can occur due to malformed JWT tokens,
 * missing claims, or invalid signatures.
 * This exception is used to indicate that the JWT
 * payload is invalid and cannot be processed.
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@StandardException
public class JwtParseException extends RuntimeException {
}
