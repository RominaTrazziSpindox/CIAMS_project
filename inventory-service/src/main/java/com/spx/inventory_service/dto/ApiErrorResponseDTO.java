package com.spx.inventory_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String message;
    private String action;
    private String requestPath;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy HH:mm:ss"
    )
    private LocalDateTime timestamp;
}

