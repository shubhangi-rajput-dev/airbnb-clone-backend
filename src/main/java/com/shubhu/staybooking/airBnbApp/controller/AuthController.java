package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.LoginDto;
import com.shubhu.staybooking.airBnbApp.dto.LoginResponseDto;
import com.shubhu.staybooking.airBnbApp.dto.SignUpRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.UserDto;
import com.shubhu.staybooking.airBnbApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;

/**
  REST controller responsible for handling user authentication requests.

  <p>Provides endpoints for user registration, authentication, and
  access token refresh using JWT-based authentication.</p>

  <p>All business logic is delegated to the {@link AuthService}.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Service responsible for authentication operations. */
    private final AuthService authService;

    /** Cookie name used for storing refresh tokens. */
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    /**
     * Registers a new user in the system.
     *
     * <p>Accepts the user's registration details, creates a new user account,
     * and returns the registered user's information.</p>
     *
     * @param signUpRequestDto the user registration request containing
     *                         the required sign-up details
     * @return a {@link ResponseEntity} containing the created user's details
     *         with HTTP status {@code 201 CREATED}
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(
            @Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * <p>On successful authentication, an access token is returned in the
     * response body, while the refresh token is stored as an HTTP-only cookie
     * to enhance security.</p>
     *
     * @param loginDto the user login credentials
     * @param httpServletResponse the outgoing HTTP response used to add the
     *                            refresh token cookie
     * @return a {@link ResponseEntity} containing the generated access token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginDto loginDto,
            HttpServletResponse httpServletResponse) {
        String[] tokens = authService.login(loginDto);
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, tokens[1]);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "Strict");

        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    /**
     * Generates a new access token using the refresh token stored in the
     * client's cookies.
     *
     * <p>If the refresh token is missing or invalid, an authentication
     * exception is thrown.</p>
     *
     * @param request the incoming HTTP request containing the refresh token
     *                cookie
     * @return a {@link ResponseEntity} containing the newly generated
     *         access token
     * @throws AuthenticationServiceException if the refresh token cookie
     *                                        is not found
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new AuthenticationServiceException("Refresh token not found");
        }

        String refreshToken = Arrays.stream(cookies).
                filter(cookie -> REFRESH_TOKEN_COOKIE.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() ->
                        new AuthenticationServiceException("Refresh token not found inside the Cookies"));
        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }
}
