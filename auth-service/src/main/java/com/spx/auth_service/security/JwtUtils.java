package com.spx.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Utility class for handling JWT operations:
 * - Token generation
 * - Incoming token validation
 * - Claims extraction for internal use
 *
 * This class represents the token authority
 */
@Component
@Slf4j
public class JwtUtils {

    // JWT Properties (defined in application.yaml)

    /**
     * Base64-encoded secret key used to sign the JWT.
     * Must be at least 256 bits for HS256.
     */
    @Value("${spring.security.jwt.secret}")  // Same path of yaml property
    private String jwtSecretKey;

    /**
     * Token expiration time.
     *
     * */
    @Value("${spring.security.jwt.expiration}")  // Same path of yaml property
    private Long expiration;

    private SecretKey key;

    // PostConstructor: generates the secret Key only AFTER the class JwtUtil is instantiated
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey) );
    }


    /* ***** MAIN JWT FUNCTIONS ***** */

    // Function to generate a new token with claims

    /**
     * Generates a signed JWT containing username and roles.
     *
     * @param username authenticated user's username
     * @param roles    list of user roles (without ROLE_ prefix)
     * @return signed JWT token
     */
    public String generateToken(String username, List<String> roles) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .claim("roles", roles)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validates a received JWT token.
     * Check if it is valid
     *
     * Validation includes:
     * - Signature verification
     * - Token structure validation
     * - Expiration check
     *
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    /* ***** OTHER JWT FUNCTIONS ****** */

    /**
     * Parses and returns all JWT claims.
     * Centralized parsing avoids duplicated logic and inconsistencies.
     *
     * @param token JWT token string
     * @return JWT claims payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Get the username from a JWT token
    public String getUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Get the expiration date from a JWT token
    public Date getExpirationFromToken(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Get the expiration date from a JWT token as LocalDateTime
    public LocalDateTime getExpirationLocalDateTimeFromToken(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}

