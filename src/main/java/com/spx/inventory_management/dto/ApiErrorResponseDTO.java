package com.spx.inventory_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Api error response dto.
 * Format of server JSON response when the application encounters an error or an exception.
 */
@Data
@AllArgsConstructor
public class ApiErrorResponseDTO {

    private int status;
    private String errorTitle;

    @NotBlank(message = "There is an error. Please, try again.")
    private String message;
    private String URIpath;
    private LocalDateTime timestamp;
}

