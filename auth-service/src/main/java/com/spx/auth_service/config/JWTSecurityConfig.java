package com.spx.auth_service.config;

import com.spx.auth_service.security.JwtAccessDeniedHandler;
import com.spx.auth_service.security.JwtAuthTokenFilter;
import com.spx.auth_service.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class JWTSecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;

    // Constructor injection
    public JWTSecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler, JwtAccessDeniedHandler accessDeniedHandler, JwtAuthTokenFilter jwtAuthTokenFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // REST API → no CSRF
                .csrf(csrf -> csrf.disable())

                // CORS gestito altrove (gateway / reverse proxy)
                .cors(cors -> cors.disable())

                // Stateless session (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Centralized exception handling
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Error endpoint
                        .requestMatchers("/error").permitAll()

                        // PUBLIC endpoints
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // ADMIN-only
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // AUTHENTICATED write operations
                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/**").authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )


                // JWT filter (must be BEFORE UsernamePasswordAuthenticationFilter)
                .addFilterBefore(
                        jwtAuthTokenFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}


