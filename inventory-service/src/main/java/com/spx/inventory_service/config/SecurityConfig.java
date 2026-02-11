package com.spx.inventory_service.config;

import com.spx.inventory_service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for inventory-service.
 *
 * - inventory-service does NOT authenticate users
 * - inventory-service trusts JWT issued by auth-service
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // No CSRF: stateless REST API
                .csrf(csrf -> csrf.disable())

                // No HTTP session: JWT is stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints (if any)
                        .requestMatchers("/error").permitAll()

                        // READ operations: authenticated users
                        .requestMatchers(HttpMethod.GET, "/**").authenticated()

                        // WRITE operations
                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/**").authenticated()

                        // DELETE operations: ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                )

                // =====================================================
                // JWT FILTER
                // =====================================================

                // Register JWT filter BEFORE Spring's auth filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Disable default login mechanisms
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}