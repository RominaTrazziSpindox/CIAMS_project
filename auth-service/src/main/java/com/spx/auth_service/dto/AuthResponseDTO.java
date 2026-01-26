package com.spx.auth_service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthResponseDTO {

    private String token;
    private String tokenType = "Bearer";

    // Constructor
    public AuthResponseDTO(String token) {
        this.token = token;
    }

}









