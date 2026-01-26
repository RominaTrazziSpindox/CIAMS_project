package com.spx.auth_service.dto;

import com.spx.auth_service.models.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50)
    private String username;


    private Set<Role> roles;

}
