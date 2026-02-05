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
 *  Utility class for handling JWT operations:
 * - Validate incoming JWT tokens
 * - Extract identity (username) and authorization data (roles)
 *
 * This class trusts JWTs issued by auth-service and does not generate tokens.
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


    /* ***** MAIN JWT FUNCTIONS ***** */

    /**
     * Validates a JWT token.
     *
     * Validation includes:
     * - Signature verification
     * - Token structure validation
     * - Expiration check
     *
     * @param token raw JWT string
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

    /* This method:
        1. Reads the claim "roles" from JWT
        2. Interprets as a List
        3. Retrieves as a List<String> thanks to @SuppressWarnings and avoids warning
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    // Get the expiration date from a JWT token
    public Date getExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}
