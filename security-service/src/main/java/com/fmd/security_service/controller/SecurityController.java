package com.fmd.security_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SecurityController handles security-related endpoints.
 * It provides an endpoint for user authentication.
 * 
 * This service authenticates users using Spring Security to validate
 * the JWT token passed in the Authorization header.
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/security")
public class SecurityController {

    /**
     * Endpoint to authenticate a user.
     * 
     * This method is called when a user attempts to authenticate.
     * It returns a success message if the authentication is successful.
     * 
     * @return a success message indicating successful authentication
     */
    @PostMapping("/authenticate")
    @PreAuthorize("hasAnyAuthority('Admin', 'User')")
    public String postMethodName(org.springframework.security.core.Authentication authentication) {
        // Retrieve the user details from the SecurityContext (set by JWT filter)
        String username;

        // If using UserDetails, extract username
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            // Fallback to authentication name
            username = authentication.getName();
        }

        log.info("User '{}' authenticated successfully", username);
        return "User '" + username + "' authenticated successfully";
    }

}
