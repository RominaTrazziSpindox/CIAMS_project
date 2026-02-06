package com.spx.inventory_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/**
 * Inventory-service trusts JWT issued by auth-service. No user lookup is performed here.
 * This class:
 * - intercept incoming HTTP requests
 * - extract JWT from Authorization header
 * - validate JWT
 * - build Spring Security Authentication
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // STEP 1: Extract JWT from Authorization header from HTTP request
            String jwt = extractJwt(request);

            // STEP 2: Validate JWT (signature and expiration)
            if (jwt != null && jwtUtils.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {

                // STEP 3: Extract identity claims from JWT
                String username = jwtUtils.getUsernameFromToken(jwt);
                List<String> roles = jwtUtils.getRolesFromToken(jwt);

                // STEP 4: Map roles to Spring Security authorities
                var authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

                // STEP 5: Build Authentication for the current request
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // STEP 6: Store Authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            log.error("JWT authentication error", e);
        }

        // STEP 7: Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from Authorization header.
     */
    private String extractJwt(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
