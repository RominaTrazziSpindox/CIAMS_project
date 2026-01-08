package com.spx.inventory_management.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // REST API → CSRF (Cross-Site Request Forgery) off
            .csrf(csrf -> csrf.disable())

            // No HTTP session (stateless)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )

            // AuthenticationEntryPoint (errors: 401 - Unauthorized & 403 - Forbidden permission )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // WRITE operations require authentication
                .requestMatchers(HttpMethod.POST, "/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                // READ operations are public
                .anyRequest().permitAll()
            )

            // Basic Authentication
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Type of users saved in RAM memory
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password("{noop}user")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}




