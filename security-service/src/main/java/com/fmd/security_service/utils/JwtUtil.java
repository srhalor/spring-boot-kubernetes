package com.fmd.security_service.utils;

import java.time.Instant;
import java.util.Base64;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmd.security_service.dto.JwtPayload;
import com.fmd.security_service.exception.JwtAuthenticationException;
import com.fmd.security_service.exception.JwtParseException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling JWT operations such as extracting username, validating tokens,
 * and parsing JWT payloads.
 */
@Slf4j
@UtilityClass
public class JwtUtil {


    /**
     * Extracts the payload from the JWT token and parses it into a JwtPayload object.
     *
     * @param token the JWT token
     * @return the parsed JwtPayload object
     * @throws IllegalArgumentException if the token is invalid
     */
    public JwtPayload validateAndExtractPayload(String token) {
        log.debug("Validating JWT token structure");

        log.trace("Null and empty check for JWT token");
        // Check if the token is null or empty
        if (!StringUtils.hasText(token)) {
            throw new JwtParseException("JWT token is null or empty");
        }

        log.trace("Token starts with 'Bearer ' check");
        // Check if the token starts with "Bearer "
        if (!token.startsWith("Bearer ")) {
            throw new JwtParseException("JWT token does not start with 'Bearer ': " + token.substring(0, 7));
        }

        log.trace("Checking if token has 3 parts");
        // Split the token into parts
        var parts = token.split("\\.");
        // Check if the token has 3 parts
        if (parts.length != 3) {
            throw new JwtParseException("JWT token does not have 3 parts");
        }

        log.trace("Decoding payload part of JWT token");
        // Decode the payloadPart part of the JWT token
        var payloadPart = new String(Base64.getUrlDecoder().decode(parts[1]));

        // parse the payload part into a JwtPayload object
        var jwtPayload = parsePayload(payloadPart);

        log.debug("Validating JWT payload");
        // Validate the JWT payload, throws an exception if invalid
        validatePayload(jwtPayload);

        return jwtPayload;
    }

    /**
     * Parses the JWT payload string into a JwtPayload object.
     *
     * @param payloadString the JWT payload as a JSON string
     * @return the JwtPayload object
     * @throws JwtParseException if parsing fails
     */
    private JwtPayload parsePayload(String payloadString) {

        log.debug("Parsing JWT payload");
        try {

            // Create an ObjectMapper instance for JSON parsing
            ObjectMapper mapper = new ObjectMapper();
            // Register the JavaTimeModule to handle Java 8 date/time types
            mapper.registerModule(new JavaTimeModule());
            // Disable writing dates as timestamps
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            // Parse the JSON string into a JwtPayload object using Jackson
            JwtPayload jwtPayload = mapper.readValue(payloadString, JwtPayload.class);
            log.trace("Parsed JWT payload: {}", jwtPayload);

            return jwtPayload;
        } catch (Exception e) {
            log.error("Failed to parse JWT payload: ", e);
            throw new JwtParseException("Failed to parse JWT payload", e);
        }
    }

    /**
     * validate subject and expiration date.
     *
     * @param jwtPayload the JWT payload to validate
     */
    private void validatePayload(JwtPayload jwtPayload) {

        // Check if the JWT payload is null
        if (jwtPayload == null) {
            throw new JwtAuthenticationException("JWT payload is null");
        }

        log.trace("Validating token expiration");
        // Check if the JWT payload has a valid expiration date
        if (jwtPayload.expiration() == null) {
            throw new JwtAuthenticationException("JWT payload expiration date is null");
        }

        // Check if the JWT payload has expired
        if (jwtPayload.expiration().isBefore(Instant.now())) {
            throw new JwtAuthenticationException("JWT token has expired");
        }

        log.trace("Validating token subject");
        // Check if the JWT payload has a valid subject
        if (!StringUtils.hasText(jwtPayload.subject())) {
            throw new JwtAuthenticationException("JWT payload subject is null or empty");
        }
        

    }
}
