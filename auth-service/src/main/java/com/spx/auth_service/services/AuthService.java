package com.spx.auth_service.services;

import com.spx.auth_service.dto.AuthRequestDTO;
import com.spx.auth_service.dto.AuthResponseDTO;
import com.spx.auth_service.models.Role;
import com.spx.auth_service.models.User;
import com.spx.auth_service.repositories.UserRepository;
import com.spx.auth_service.security.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;


    // Constructor
    public AuthService(UserRepository userRepository,AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,JWTUtils jwtUtils) {

        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // ==========================================================
    // REGISTER
    // ==========================================================

    /**
     * Registers a new user.
     *
     * Business rules:
     * - Username must be unique
     * - Password is always stored hashed
     * - Default role is USER
     *
     * @param request registration data
     */
    public void register(AuthRequestDTO request) {

        // STEP 1: Check if there are duplicate usernames
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration attempt with existing username: {}", request.getUsername());
            throw new IllegalStateException("Username already exists");
        }

        // STEP 2: Build User domain object
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.USER))
                .build();

        // STEP 3: Persist user
        userRepository.save(user);

        log.info("User '{}' successfully registered", user.getUsername());
    }

    // ==========================================================
    // LOGIN
    // ==========================================================

    /**
     * Authenticates a user and generates a JWT.
     *
     * Important notes:
     * - Credential validation is delegated to Spring Security
     * - AuthenticationException is handled by Spring Security (401)
     * - Roles are loaded from DB
     *
     * @param request login credentials
     * @return AuthResponseDTO containing JWT and user roles
     */
    public AuthResponseDTO login(AuthRequestDTO request) {

        // STEP 1: Delegate authentication to Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // STEP 2: Authentication succeeded -> extract username
        String username = authentication.getName();

        // STEP 3: Load User entity from DB
        // This should never fail if authentication succeeded.
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("Authenticated user '{}' not found in DB", username);
            return new IllegalStateException("User not found after authentication");
        });

        // STEP 4: Generate JWT
        String token = jwtUtils.generateToken(
                user.getUsername(),
                user.getRoles().stream().map(Enum::name).toList()
        );

        log.info("User '{}' successfully authenticated", username);

        // STEP 5: Build response DTO (roles are informational for the client)
        return new AuthResponseDTO(token, user.getRoles());
    }

    // ==========================================================
    // DELETE USER
    // ==========================================================

    /**
     * Deletes a user by username.
     *
     * @param username username to delete
     */
    public void deleteByUsername(String username) {

        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.deleteByUsername(username);

        log.info("User '{}' deleted", username);
    }

    // ==========================================================
    // QUERY USERS BY ROLE (PAGING)
    // ==========================================================

    /**
     * Returns a paginated list of usernames having the given role.
     *
     * @param role role to filter by
     * @param pageable paging information
     * @return page of usernames
     */
    public Page<String> findUsernamesByRole(Role role, Pageable pageable) {
        return userRepository.findByRoles(role, pageable).map(User::getUsername);
    }
}
