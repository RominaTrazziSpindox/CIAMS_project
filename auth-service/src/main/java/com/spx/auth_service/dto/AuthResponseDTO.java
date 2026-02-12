package com.spx.auth_service.dto;


import com.spx.auth_service.models.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
public class AuthResponseDTO {

    private String token;
    private String tokenType;
    private String username;
    private Set<Role> roles;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expiresAt;
}









