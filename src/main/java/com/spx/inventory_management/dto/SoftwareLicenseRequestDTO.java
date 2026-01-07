package com.spx.inventory_management.dto;

import java.io.Serializable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SoftwareLicenseRequestDTO implements Serializable {

    @NotBlank
    private String softwareName;

    // Null = no limits
    @Min(1)
    private Integer maxInstallations;

    @NotNull
    private LocalDate expirationDate;
}