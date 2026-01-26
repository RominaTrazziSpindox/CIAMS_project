package com.spx.auth_service.dto;


import lombok.Data;

@Data
public class AuthResponseDTO {

    private String token;

    // Constructor
    public AuthResponseDTO(String token) {
        this.token = token;
    }

}









