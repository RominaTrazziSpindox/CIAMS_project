package com.spx.auth_service.services;

import com.spx.auth_service.dto.ApiErrorResponseDTO;
import com.spx.auth_service.dto.AuthRequestDTO;
import com.spx.auth_service.dto.AuthResponseDTO;
import com.spx.auth_service.models.Role;
import com.spx.auth_service.models.User;
import com.spx.auth_service.repositories.UserRepository;
import com.spx.auth_service.security.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // ==========================================================
    // REGISTER
    // ==========================================================

    public void register(AuthRequestDTO request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.USER))
                .build();

        userRepository.save(user);

        log.info("User '{}' successfully registered", user.getUsername());
    }

    // ==========================================================
    // LOGIN
    // ==========================================================

    public AuthResponseDTO login(AuthRequestDTO request) {

        // STEP 1: Perform the user authentication by the authentication manager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                String username = authentication.getName();

        // STEP 2: Load User details
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalStateException("User authenticated but not found in database"));

        // STEP 3: Generate token
        String token = jwtUtils.generateToken(
                user.getUsername(),
                user.getRoles().stream().map(Enum::name).toList()
        );

        // STEP 4: Extract expiration date from token
        Instant expiresAt = jwtUtils
                .getExpirationFromToken(token)
                .toInstant();


        log.info("User '{}' successfully authenticated", username);

        return new AuthResponseDTO(token,"Bearer", user.getUsername(), user.getRoles(), expiresAt);

    }
}
