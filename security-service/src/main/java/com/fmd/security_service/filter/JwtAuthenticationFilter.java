package com.fmd.security_service.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fmd.security_service.exception.JwtAuthenticationException;
import com.fmd.security_service.exception.JwtParseException;
import com.fmd.security_service.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter for JWT authentication. Extracts and validates JWT from the
 * Authorization header,
 * and sets the authentication in the security context if valid.
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Filters each request to check for a valid JWT token in the Authorization
     * header.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Authenticate the request by validating the JWT token
            authenticateRequest(request);

        } catch (JwtAuthenticationException e) {
            log.warn("Error while validating JWT token : {}", e.getMessage());
        } catch (JwtParseException e) {
            log.warn("Error while parsing JWT token : {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unhandled error in JWT filter", e);
        }

        // Continue the filter chain for the next filter or resource
        filterChain.doFilter(request, response);
    }

    /**
     * Authenticates the request by validating the JWT token in the Authorization
     * header.
     *
     * @param request the HTTP request
     */
    private static void authenticateRequest(HttpServletRequest request) {
        log.debug("Processing JWT authentication filter");

        // Retrieve the Authorization header from the request
        var authHeader = request.getHeader("Authorization");

        log.info("Authenticating request with JWT token");
        // Validate and extract the JWT payload from the Authorization header
        var jwtPayload = JwtUtil.validateAndExtractPayload(authHeader);
        log.info("User [{}] authenticated successfully", jwtPayload.subject());

        // If subject is present, authentication is not already set, and token is valid,
        // set authentication
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Setting authentication for user: [{}]", jwtPayload.subject());

            // Convert roles to GrantedAuthority list
            List<SimpleGrantedAuthority> authorities = jwtPayload.roles() == null ? List.of()
                    : jwtPayload.roles().stream().map(SimpleGrantedAuthority::new).toList();

            // Create a new User object with the subject and roles from the JWT payload
            var user = new User(jwtPayload.subject(), "", authorities);
            // Create an authentication token and set it in the security context
            var authToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            // If subject is present but token is invalid or authentication is already set
            log.warn("Authentication already set for user: [{}]", jwtPayload.subject());
        }
    }
}
