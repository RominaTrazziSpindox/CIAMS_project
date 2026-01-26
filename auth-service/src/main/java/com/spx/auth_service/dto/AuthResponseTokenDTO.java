package com.spx.auth_service.dto;


import lombok.Data;

@Data
public class AuthResponseTokenDTO {

    private String token;

    // Constructor
    public AuthResponseTokenDTO(String token) {
        this.token = token;
    }

}









