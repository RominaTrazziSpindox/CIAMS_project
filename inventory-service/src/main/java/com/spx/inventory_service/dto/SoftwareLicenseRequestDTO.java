package com.spx.inventory_service.dto;

import java.io.Serializable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class SoftwareLicenseRequestDTO implements Serializable {

    @NotBlank
    private String softwareName;

    // Null = no limits
    @Min(1)
    private Integer maxInstallations;

    @NotNull
    private LocalDate expirationDate;
}