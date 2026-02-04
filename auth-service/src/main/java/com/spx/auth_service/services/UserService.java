package com.spx.auth_service.services;

import com.spx.auth_service.exceptions.ResourceNotFoundException;
import com.spx.auth_service.models.Role;
import com.spx.auth_service.models.User;
import com.spx.auth_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ==========================================================
    // READ
    // ==========================================================

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
            new ResourceNotFoundException("User with username '" + username + "' not found"));
    }

    public boolean userExists(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(username + "must not be null or blank");
        }
        return userRepository.existsByUsername(username);
    }

    // ==========================================================
    // DELETE
    // ==========================================================

    public void deleteByUsername(String username) {

        long deleted = userRepository.deleteByUsername(username);

        if (deleted == 0) {
            throw new ResourceNotFoundException(
                    "User '%s' not found".formatted(username)
            );
        }

        log.info("User '{}' deleted", username);
    }

    // ==========================================================
    // QUERY
    // ==========================================================

    public Page<String> findUsernamesByRole(Role role, Pageable pageable) {
        return userRepository
                .findByRoles(role, pageable)
                .map(User::getUsername);
        }
    }
