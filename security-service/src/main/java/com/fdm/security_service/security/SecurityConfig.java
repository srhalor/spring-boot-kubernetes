package com.fdm.security_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fdm.security_service.exception.handler.CustomAccessDeniedHandler;
import com.fdm.security_service.exception.handler.CustomAuthenticationEntryPoint;
import com.fdm.security_service.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Security configuration class for the application.
 * <p>
 * This class configures the security settings for the application, including
 * authentication and authorization rules.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Configures the security filter chain to permit all requests and disable CSRF
     * protection.
     * <p>
     * This configuration is used when security is disabled in the application.
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain permitAllSecurityFilterChain(HttpSecurity http) throws Exception {
        log.debug("Configuring NoSecurityConfig as security is disabled");
        http.csrf(AbstractHttpConfigurer::disable);

        // Set session management to stateless (no HTTP session)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Configure custom authentication entry point for handling
        // authentication/access denied errors
        http.exceptionHandling(exception -> {
            exception.authenticationEntryPoint(customAuthenticationEntryPoint);
            exception.accessDeniedHandler(customAccessDeniedHandler);
        });

        // Configure authorization rules for HTTP requests
        http.authorizeHttpRequests(auth -> {
            // Allow unauthenticated access to /auth/** endpoints
            auth.requestMatchers("/actuator/**").permitAll();
            auth.requestMatchers("/health").permitAll();
            // Allow all OPTIONS requests (CORS preflight)
            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
            // Require authentication for all other requests
            auth.anyRequest().authenticated();
        });

        // Add the JWT authentication filter before the default
        // UsernamePasswordAuthenticationFilter
        http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // Build and return the configured SecurityFilterChain
        return http.build();
    }

    /**
     * Provides the AuthenticationManager bean.
     * <p>
     * This bean is required for authentication-related operations in the
     * application.
     * In this configuration, it simply delegates to the default
     * AuthenticationManager
     * provided by Spring Security's AuthenticationConfiguration.
     *
     * @param config the AuthenticationConfiguration instance provided by Spring
     * @return the AuthenticationManager instance
     * @throws Exception if retrieval of the AuthenticationManager fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.debug("Providing AuthenticationManager bean");
        // Return the AuthenticationManager from the configuration
        return config.getAuthenticationManager();
    }
}
