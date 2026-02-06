package com.spx.auth_service.services;

import com.spx.auth_service.dto.AuthRequestDTO;
import com.spx.auth_service.dto.AuthResponseDTO;
import com.spx.auth_service.models.Role;
import com.spx.auth_service.models.User;
import com.spx.auth_service.repositories.UserRepository;
import com.spx.auth_service.security.JwtUtils;
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
    private final JwtUtils jwtUtils;

    // Constructor injection
    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // Register
    public void register(AuthRequestDTO request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.USER))
                .build();

        userRepository.save(user);

        log.info("User '{}' successfully registered", user.getUsername());
    }

    // Login
    public AuthResponseDTO login(AuthRequestDTO request) {

        /* STEP 1: Perform the user authentication by using the authentication manager.
        It calls UserDetailsService.loadByUsername().
        If authenticate() method fails, it is launched an AuthenticationException  */
       Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // STEP 2: Extract username from authenticated principal in SecurityContext
        String username = authentication.getName();

        User user = userRepository.findByUsername(username);

        // STEP 4: Generate JWT
        String token = jwtUtils.generateToken(
                user.getUsername(),
                user.getRoles().stream().map(Enum::name).toList()
        );

        // STEP 5: Extract expiration timestamp
        Instant expiresAt = jwtUtils
                .getExpirationFromToken(token)
                .toInstant();

        log.info("User '{}' successfully authenticated", username);

        return new AuthResponseDTO(token, "Bearer", user.getUsername(), user.getRoles(), expiresAt);
    }
}
