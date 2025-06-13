package com.fmd.security_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SecurityController handles security-related endpoints.
 * It provides an endpoint for user authentication.
 * <p>
 * This service authenticates users using Spring Security to validate
 * the JWT token passed in the Authorization header.
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/authenticate")
public class SecurityController {

    /**
     * Endpoint to authenticate a user.
     * <p>
     * This method is called when a user attempts to authenticate.
     * It returns a success message if the authentication is successful.
     *
     * @return a success message indicating successful authentication
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('Admin', 'User')")
    public String postMethodName(Authentication authentication) {

        // Retrieve the user details from the SecurityContext (set by JWT filter)
        var userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        log.info("User '{}' authenticated successfully", username);
        return "User '" + username + "' authenticated successfully";
    }

}
