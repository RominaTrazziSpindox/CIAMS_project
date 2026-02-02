package com.spx.auth_service.dto;


import com.spx.auth_service.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class AuthResponseDTO {

    private String token;
    private String tokenType = "Bearer";
    private Set<Role> roles;

    // Constructor
    public AuthResponseDTO(String token, Set<Role> roles) {
        this.token = token;
        this.roles = roles;
    }

}









