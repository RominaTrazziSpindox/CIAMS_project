package com.spx.auth_service.dto;

import com.spx.auth_service.models.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {

    private String username;
    private Set<Role> roles;

}
