package com.spx.auth_service.dto;


import com.spx.auth_service.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@Getter
public class AuthResponseDTO {

    private String token;
    private String tokenType;
    private String username;
    private Set<Role> roles;
    private Instant expiresAt;
}









