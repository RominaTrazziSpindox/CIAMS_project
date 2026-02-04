package com.spx.inventory_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT utility class for inventory-service.
 *
 * RESPONSIBILITY:
 * - validate incoming JWT tokens
 * - extract identity (username) and authorization data (roles)
 */
@Component
@Slf4j
public class JwtUtils {

    /**
     * Base64-encoded shared secret.
     * Must be the same used by auth-service.
     */
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    private SecretKey key;

    /**
     * Initialize signing key once at startup.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    /**
     * Validates a JWT token.
     *
     * Validation includes:
     * - signature verification
     * - token structure validation
     * - expiration check
     *
     * @param token raw JWT string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired");
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token");
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or null");
        } catch (Exception e) {
            log.error("Unexpected JWT validation error", e);
        }
        return false;
    }

    // =========================================================
    // EXTRACTION
    // =========================================================

    /**
     * Extract username (subject) from JWT.
     *
     * @throws JwtException if token is invalid
     */
    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract roles from JWT.
     *
     * @throws JwtException if token is invalid
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    /**
     * Extract expiration date from JWT.
     *
     * @throws JwtException if token is invalid
     */
    public Date getExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // =========================================================
    // INTERNAL
    // =========================================================

    /**
     * Centralized JWT parsing.
     * Any parsing-related change must be done here.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
