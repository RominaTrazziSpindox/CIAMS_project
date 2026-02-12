package com.spx.inventory_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spx.inventory_service.dto.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // Constructor injection
    private final ObjectMapper objectMapper;


    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        // Creating a new ApiResponseDTO
        ApiErrorResponseDTO error = new ApiErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Access denied - insufficient permissions",
                "Contact an administrator if access is required",
                "uri=" + request.getRequestURI(),
                LocalDateTime.now()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), error);
    }

}

