package com.spx.auth_service.config;

import com.spx.auth_service.models.Role;
import com.spx.auth_service.models.User;
import com.spx.auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeedRunnerConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Take values from .env file
        String username = System.getenv("ADMIN_USERNAME_MONGODB");
        String password = System.getenv("ADMIN_PASSWORD_MONGODB");

        if (username == null || password == null) {
            log.warn("Admin seed skipped: ADMIN_USERNAME or ADMIN_PASSWORD not set");
            return;
        }

        if (userRepository.existsByUsername(username)) {
            log.info("Admin seed skipped: admin already exists");
            return;
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRoles(Set.of(Role.ADMIN));

        userRepository.save(admin);

        log.info("Admin seed completed: admin user created");
    }
}