package com.fmd.security_service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fmd.security_service.exception.handler.CustomAccessDeniedHandler;
import com.fmd.security_service.exception.handler.CustomAuthenticationEntryPoint;
import com.fmd.security_service.filter.JwtAuthenticationFilter;
import com.fmd.security_service.security.SecurityConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for {@link SecurityConfig}.
 * <p>
 * This class verifies that the security configuration is correctly applied,
 * including:
 * <ul>
 * <li>CSRF and session management settings</li>
 * <li>Exception handling and authorization rules</li>
 * <li>JWT filter placement</li>
 * <li>AuthenticationManager bean provision</li>
 * </ul>
 * Logging is used to trace test execution and configuration steps.
 * </p>
 * 
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
class SecurityConfigTest {
    private CustomAuthenticationEntryPoint entryPoint;
    private CustomAccessDeniedHandler accessDeniedHandler;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        entryPoint = mock(CustomAuthenticationEntryPoint.class);
        accessDeniedHandler = mock(CustomAccessDeniedHandler.class);
        securityConfig = new SecurityConfig(entryPoint, accessDeniedHandler);
    }

    /**
     * Verifies that permitAllSecurityFilterChain configures HttpSecurity as
     * expected.
     *
     * <p>
     * Explanation of important lines for future reference:
     * </p>
     * <ul>
     * <li><b>when(http.csrf(any())).thenReturn(http);</b> - Mocks disabling CSRF
     * protection in the security configuration.</li>
     * <li><b>when(http.sessionManagement(any())).thenReturn(http);</b> - Mocks
     * setting session management to stateless, ensuring no HTTP session is
     * created.</li>
     * <li><b>when(http.exceptionHandling(any())).thenReturn(http);</b> - Mocks
     * configuring custom exception handling for authentication and access denied
     * errors.</li>
     * <li><b>when(http.authorizeHttpRequests(any())).thenReturn(http);</b> - Mocks
     * setting up authorization rules for HTTP requests.</li>
     * <li><b>when(http.addFilterBefore(any(), any())).thenReturn(http);</b> - Mocks
     * adding the JwtAuthenticationFilter before the
     * UsernamePasswordAuthenticationFilter in the filter chain.</li>
     * <li><b>when(http.build()).thenReturn(mock(DefaultSecurityFilterChain.class));</b>
     * - Mocks building and returning the SecurityFilterChain object.</li>
     * <li><b>SecurityFilterChain chain =
     * securityConfig.permitAllSecurityFilterChain(http);</b> - Calls the method
     * under test to configure the security filter chain.</li>
     * <li><b>assertNotNull(chain);</b> - Asserts that the filter chain is not null,
     * indicating successful configuration.</li>
     * <li><b>verify(...)</b> - Verifies that each configuration method was called
     * as expected, ensuring the security setup is correct.</li>
     * </ul>
     */
    @Test
    void testPermitAllSecurityFilterChain_configuresHttpSecurity() throws Exception {
        log.info("Testing permitAllSecurityFilterChain configuration");
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        // Mock chained methods
        // Mocks disabling CSRF protection in the security configuration.
        when(http.csrf(any())).thenReturn(http);
        // Mocks setting session management to stateless, ensuring no HTTP session is
        // created.
        when(http.sessionManagement(any())).thenReturn(http);
        // Mocks configuring custom exception handling for authentication and access
        // denied errors.
        when(http.exceptionHandling(any())).thenReturn(http);
        // Mocks setting up authorization rules for HTTP requests.
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        // Mocks adding the JwtAuthenticationFilter before the
        // UsernamePasswordAuthenticationFilter in the filter chain.
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        // Mocks building and returning the SecurityFilterChain object.
        when(http.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

        // Calls the method under test to configure the security filter chain.
        SecurityFilterChain chain = securityConfig.permitAllSecurityFilterChain(http);
        log.debug("SecurityFilterChain built: {}", chain);
        // Asserts that the filter chain is not null, indicating successful
        // configuration.
        assertNotNull(chain);
        // Verifies that each configuration method was called as expected, ensuring the
        // security setup is correct.
        verify(http).csrf(any());
        verify(http).sessionManagement(any());
        verify(http).exceptionHandling(any());
        verify(http).authorizeHttpRequests(any());
        verify(http).addFilterBefore(any(JwtAuthenticationFilter.class),
                eq(UsernamePasswordAuthenticationFilter.class));
        verify(http).build();
    }

    /**
     * Verifies that authenticationManager returns the correct AuthenticationManager
     * from the config.
     *
     * <p>
     * Explanation of important lines for future reference:
     * </p>
     * <ul>
     * <li><b>when(config.getAuthenticationManager()).thenReturn(manager);</b> -
     * Mocks the AuthenticationConfiguration to return a mock
     * AuthenticationManager.</li>
     * <li><b>AuthenticationManager result =
     * securityConfig.authenticationManager(config);</b> - Calls the method under
     * test to retrieve the AuthenticationManager bean.</li>
     * <li><b>assertEquals(manager, result);</b> - Asserts that the returned
     * AuthenticationManager is the same as the mocked one.</li>
     * <li><b>verify(config).getAuthenticationManager();</b> - Verifies that the
     * getAuthenticationManager method was called on the configuration object.</li>
     * </ul>
     */
    @Test
    void testAuthenticationManager_returnsManagerFromConfig() throws Exception {
        log.info("Testing authenticationManager bean provision");
        // Mocks the AuthenticationConfiguration to return a mock AuthenticationManager.
        AuthenticationManager manager = mock(AuthenticationManager.class);
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        when(config.getAuthenticationManager()).thenReturn(manager);
        // Calls the method under test to retrieve the AuthenticationManager bean.
        AuthenticationManager result = securityConfig.authenticationManager(config);
        log.debug("AuthenticationManager returned: {}", result);
        // Asserts that the returned AuthenticationManager is the same as the mocked
        // one.
        assertEquals(manager, result);
        // Verifies that the getAuthenticationManager method was called on the
        // configuration object.
        verify(config).getAuthenticationManager();
    }
}
