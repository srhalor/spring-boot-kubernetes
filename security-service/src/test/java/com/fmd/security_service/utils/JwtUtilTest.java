package com.fmd.security_service.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.fmd.security_service.dto.JwtPayload;
import com.fmd.security_service.exception.JwtAuthenticationException;
import com.fmd.security_service.exception.JwtParseException;
import com.fmd.security_service.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for {@link JwtUtil} utility class.
 * <p>
 * This class tests the JWT validation and payload extraction logic, including:
 * <ul>
 * <li>Parsing and extracting a valid JWT payload</li>
 * <li>Handling null, empty, malformed, or expired tokens</li>
 * <li>Ensuring correct exceptions are thrown for invalid scenarios</li>
 * </ul>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
class JwtUtilTest {

    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc" +
            "3MiOiJUZXN0IiwiaWF0IjoxNzQ3OTI3NjkyLCJleHAiOjE3Nzk0NjM2OTcsImF1ZCI6" +
            "ImF1ZGlhbmNlIiwic3ViIjoidXNlcm5hbWUiLCJuYW1lIjoiSm9obm55Iiwicm9sZXM" +
            "iOlsiVXNlciIsIkFkbWluIl19.2gS8pFXJuQ1u19GjDp7UcyTRVVlmBWNnJSukjDz2ENQ";

    /**
     * Tests that a valid JWT token is parsed correctly and all payload fields are
     * extracted as expected.
     */
    @Test
    void testValidateAndExtractPayload_validToken() {
        log.info("Testing valid JWT token extraction");
        JwtPayload payload = JwtUtil.validateAndExtractPayload(VALID_TOKEN);
        log.debug("Extracted payload: {}", payload);
        assertEquals("username", payload.subject());
        assertEquals("Johnny", payload.name());
        assertEquals("Test", payload.issuer());
        assertEquals("audiance", payload.audience());
        assertNotNull(payload.expiration());
        assertTrue(payload.roles().contains("User"));
        assertTrue(payload.roles().contains("Admin"));
    }

    /**
     * Tests that passing a null token throws a JwtParseException.
     */
    @Test
    void testValidateAndExtractPayload_nullToken() {
        log.info("Testing null token");
        assertThrows(JwtParseException.class, () -> JwtUtil.validateAndExtractPayload(null));
    }

    /**
     * Tests that passing an empty token throws a JwtParseException.
     */
    @Test
    void testValidateAndExtractPayload_emptyToken() {
        log.info("Testing empty token");
        assertThrows(JwtParseException.class, () -> JwtUtil.validateAndExtractPayload(""));
    }

    /**
     * Tests that a token missing the 'Bearer ' prefix throws a JwtParseException.
     */
    @Test
    void testValidateAndExtractPayload_missingBearer() {
        log.info("Testing token missing 'Bearer ' prefix");
        String token = VALID_TOKEN.replace("Bearer ", "");
        assertThrows(JwtParseException.class, () -> JwtUtil.validateAndExtractPayload(token));
    }

    /**
     * Tests that a token with an invalid number of parts throws a
     * JwtParseException.
     */
    @Test
    void testValidateAndExtractPayload_invalidParts() {
        log.info("Testing token with invalid number of parts");
        String token = "Bearer part1.part2";
        assertThrows(JwtParseException.class, () -> JwtUtil.validateAndExtractPayload(token));
    }

    /**
     * Tests that a token with a malformed Base64 payload throws a
     * JwtParseException.
     */
    @Test
    void testValidateAndExtractPayload_malformedBase64() {
        log.info("Testing token with malformed Base64 payload");
        String token = "Bearer part1.invalid_base64.part3";
        assertThrows(JwtParseException.class, () -> JwtUtil.validateAndExtractPayload(token));
    }

    /**
     * Tests that a token with an invalid JSON payload throws a JwtParseException.
     */
    @Test
    void testValidateAndExtractPayload_invalidJsonPayload() {
        log.info("Testing token with invalid JSON payload");
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
        String payload = Base64.getUrlEncoder().encodeToString("not a json".getBytes());
        String signature = "sig";
        String token = "Bearer " + header + "." + payload + "." + signature;
        assertThrows(JwtParseException.class, () -> JwtUtil.validateAndExtractPayload(token));
    }

    /**
     * Tests that a token with an expired expiration date throws a
     * JwtAuthenticationException.
     */
    @Test
    void testValidateAndExtractPayload_expiredToken() {
        log.info("Testing token with expired expiration date");
        String expiredPayload = "{\"sub\":\"user\",\"exp\":" + (Instant.now().getEpochSecond() - 1000) + "}";
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
        String payload = Base64.getUrlEncoder().encodeToString(expiredPayload.getBytes());
        String signature = "sig";
        String token = "Bearer " + header + "." + payload + "." + signature;
        assertThrows(JwtAuthenticationException.class, () -> JwtUtil.validateAndExtractPayload(token));
    }

    /**
     * Tests that a token with a null subject throws a JwtAuthenticationException.
     */
    @Test
    void testValidateAndExtractPayload_nullSubject() {
        log.info("Testing token with null subject");
        String payloadJson = "{\"exp\": " + (Instant.now().getEpochSecond() + 10000) + "}";
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
        String payload = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes());
        String signature = "sig";
        String token = "Bearer " + header + "." + payload + "." + signature;
        assertThrows(JwtAuthenticationException.class, () -> JwtUtil.validateAndExtractPayload(token));
    }

    /**
     * Tests that a token with a null expiration throws a
     * JwtAuthenticationException.
     */
    @Test
    void testValidateAndExtractPayload_nullExpiration() {
        log.info("Testing token with null expiration");
        String payloadJson = "{\"sub\":\"user\"}";
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
        String payload = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes());
        String signature = "sig";
        String token = "Bearer " + header + "." + payload + "." + signature;
        assertThrows(JwtAuthenticationException.class, () -> JwtUtil.validateAndExtractPayload(token));
    }
}
