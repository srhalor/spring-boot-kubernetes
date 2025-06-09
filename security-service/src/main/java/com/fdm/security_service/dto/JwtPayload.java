package com.fdm.security_service.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JWT payload record representing the claims in a JWT token.
 * <p>
 * Includes issuer, issuedAt, expiration, audience, subject, name, and roles.
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
public record JwtPayload(
                // JWT issuer (who issued the token)
                @JsonProperty("iss") String issuer,
                // JWT issued at (timestamp in seconds since epoch)
                @JsonProperty("iat") Instant issuedAt,
                // JWT expiration (timestamp in seconds since epoch)
                @JsonProperty("exp") Instant expiration,
                // JWT audience (intended recipient)
                @JsonProperty("aud") String audience,
                // JWT subject (the user or entity the token refers to)
                @JsonProperty("sub") String subject,
                // Name of the user/entity
                @JsonProperty("name") String name,
                // Roles assigned to the user/entity
                @JsonProperty("roles") List<String> roles) {
}
