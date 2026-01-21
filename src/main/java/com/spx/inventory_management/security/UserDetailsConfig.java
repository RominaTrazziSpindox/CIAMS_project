package com.spx.inventory_management.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
public class UserDetailsConfig {

    @Value("${DB_ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${DB_ADMIN_PASSWORD}")
    private String adminPassword;

    @Value("${DB_USER_USERNAME}")
    private String userUsername;

    @Value("${DB_USER_PASSWORD}")
    private String userPassword;

    /**
     * BCrypt library to hash users' passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Users stored in RAM (DEV / TEST purpose)
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        // --- ADMIN ROLE --
        UserDetails admin = User.withUsername(adminUsername)
                .password(encoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        // --- USER ROLE (optional - only with dev profile) ---
        if (userUsername != null && !userUsername.isBlank() && userPassword != null && !userPassword.isBlank()) {

            UserDetails user = User.withUsername(userUsername)
                    .password(encoder.encode(userPassword))
                    .roles("USER")
                    .build();

            return new InMemoryUserDetailsManager(admin, user);
        }

        // Only admin profile
        return new InMemoryUserDetailsManager(admin);
    }
}