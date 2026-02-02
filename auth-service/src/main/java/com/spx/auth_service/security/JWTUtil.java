package com.spx.auth_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class JwtUtil {

    // JWT Properties (defined in application.yaml)
    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration-ms}")
    private Long expiration;

    private SecretKey key;

    // PostConstructor: generates the secret Key only AFTER the class JwtUtil is instantiated
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    // Function to generate a new token with claims
    public String generateToken(String username, List<String> roles) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .claim("roles", roles)
                .signWith(key)
                .compact();
    }

    // Get the username from a JWT token
    public String getUserFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    // Check if a received token is valid
    public boolean validateJWTtoken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;

    }
}

