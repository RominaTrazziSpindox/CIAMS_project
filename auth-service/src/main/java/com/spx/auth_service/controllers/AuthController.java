package com.spx.auth_service.controllers;

import com.spx.auth_service.dto.AuthRequestDTO;
import com.spx.auth_service.dto.AuthResponseDTO;
import com.spx.auth_service.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for authentication-related endpoints.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // Constructor injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user.
     * - receives request body
     * - delegates to AuthService
     * - returns appropriate HTTP status
     *
     * Business logic and validation errors are handled by the service
     * and mapped by GlobalExceptionHandler.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody AuthRequestDTO request) {
        authService.register(request);
    }


    /**
     * Authenticates a user and returns a JWT.
     *
     * - Credential validation is delegated to Spring Security
     * - Authentication and authorization errors are handled globally
     * - Returned roles are informational for the client (UI purposes)
     */
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO request) {
        return authService.login(request);
    }
}
