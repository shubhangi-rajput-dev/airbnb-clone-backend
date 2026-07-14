package com.shubhu.staybooking.airBnbApp.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Configures Spring Security for the application.
 *
 * <p>This configuration defines the security filter chain, password encoder,
 * authentication manager, JWT authentication filter, authorization rules,
 * and custom access denied handling used by the application.</p>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    public WebSecurityConfig(
            JWTAuthFilter jwtAuthFilter,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Configures the application's HTTP security.
     *
     * <p>Disables CSRF for stateless JWT authentication, configures stateless
     * session management, registers the JWT authentication filter, defines
     * endpoint authorization rules, and configures custom access denied handling.</p>
     *
     * @param httpSecurity the HttpSecurity object used to configure web security.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs while building the security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("HOTEL_MANAGER")
                        .requestMatchers("/bookings/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exHandlingConfig -> exHandlingConfig.accessDeniedHandler(accessDeniedHandler()));
        return httpSecurity.build();
    }

    /**
     * Creates the PasswordEncoder bean used to securely hash and verify user passwords.
     *
     * @return BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the Spring Security AuthenticationManager as a bean.
     *
     * @param configuration the authentication configuration provided by Spring Security.
     * @return configured AuthenticationManager.
     * @throws Exception if the AuthenticationManager cannot be created.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Creates a custom AccessDeniedHandler that delegates authorization failures
     * to the application's global exception handler.
     *
     * @return configured AccessDeniedHandler.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (
                request,
                response,
                accessDeniedException
        ) -> handlerExceptionResolver.resolveException(
                request,
                response,
                null,
                accessDeniedException);
    }
}
