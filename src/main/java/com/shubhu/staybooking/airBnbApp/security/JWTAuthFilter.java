package com.shubhu.staybooking.airBnbApp.security;

import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;
import org.springframework.lang.NonNull;

/**
 * JWT authentication filter that intercepts every incoming HTTP request
 * to validate the JWT access token.
 *
 * <p>This filter performs the following steps:
 * <ul>
 *     <li>Reads the Authorization header.</li>
 *     <li>Extracts the Bearer token.</li>
 *     <li>Validates and parses the JWT.</li>
 *     <li>Retrieves the authenticated user.</li>
 *     <li>Creates an Authentication object.</li>
 *     <li>Stores the Authentication in the SecurityContext.</li>
 * </ul>
 *
 * <p>If the JWT is invalid or expired, the exception is delegated to the
 * application's global exception handler using {@link HandlerExceptionResolver}.
 */
@Configuration
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JWTAuthFilter(JWTService jwtService, UserService userService,
                         @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Authenticates incoming requests using the JWT token present in the
     * Authorization header. If the token is valid, the authenticated user
     * is stored in the SecurityContext.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            // Skip JWT authentication when the request does not contain a Bearer token.
            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = requestTokenHeader.substring(7);
            Long userId = jwtService.getUserIdFromToken(token);

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userId);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Make the authenticated user available to Spring Security.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            // Delegate JWT validation errors to the global exception handler.
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
