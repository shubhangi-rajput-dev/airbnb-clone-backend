package com.shubhu.staybooking.airBnbApp.security;

import com.shubhu.staybooking.airBnbApp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service responsible for generating, signing and validating JWT access and refresh tokens.
 */
@Service
public class JWTService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    /**
     * Creates the signing key used to sign and verify JWT tokens.
     *
     * @return HMAC secret key derived from the configured secret
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a short-lived JWT access token for the authenticated user.
     *
     * @param user authenticated user
     * @return signed JWT access token
     */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 10))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Generates a long-lived JWT refresh token for the authenticated user.
     *
     * @param user authenticated user
     * @return signed JWT refresh token
     */
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30 * 6))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Extracts the user identifier from a signed JWT.
     *
     * @param token signed JWT
     * @return user identifier stored in the token subject
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

}
