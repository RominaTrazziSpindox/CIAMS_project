package com.spx.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AuthRequestDTO {

    @NotBlank (message = "Username cannot be blank")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank (message = "Password cannot be blank")
    @Size(min = 8)
    private String password;
}