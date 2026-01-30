package com.spx.auth_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spx.auth_service.dto.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // Constructor SpringBoot
    public JWTAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,HttpServletResponse response, AuthenticationException authException) throws IOException {

        // Creating a new ApiResponseDTO
        ApiErrorResponseDTO error = new ApiErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Authentication required to access this resource",
                "Provide valid authentication credentials",
                "uri=" + request.getRequestURI(),
                LocalDateTime.now()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}