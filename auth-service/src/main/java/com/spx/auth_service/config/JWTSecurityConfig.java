package com.spx.auth_service.config;

import com.spx.auth_service.security.JWTAccessDeniedHandler;
import com.spx.auth_service.security.JWTAuthTokenFilter;
import com.spx.auth_service.security.JWTAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class JWTSecurityConfig {

    @Autowired
    private JWTAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JWTAccessDeniedHandler deniedAccessHandler;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthTokenFilter authenticationJwtTokenFilter() {
        return new JWTAuthTokenFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API → CSRF (Cross-Site Request Forgery) off
                .csrf(csrf -> csrf.disable())

                // CORS OFF -> (Cross Origin Request Sharing) off
                .cors(cors -> cors.disable())

                /*  Handler for the exceptions:
                // AuthenticationEntryPoint (errors: 401 - Unauthorized & 403 - Forbidden permission ) */
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(deniedAccessHandler)
                )

                // Authorization rules based by ROLE
                .authorizeHttpRequests(auth -> auth

                        // Public method endpoints are explicitly opened
                        .requestMatchers("/auth/**").permitAll()

                        // WRITE operations require authentication
                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // READ operations are public
                        .anyRequest().permitAll()
                )

                // No HTTP session (stateless - Required for JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set custom authentication provider
                // .authenticationProvider(authenticationProvider())

                // JWT Authentication filter (= the JWTAuth filter comes before the U.P.Auth filter)
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Build the Security Filter Chain
        return http.build();

    }
}



