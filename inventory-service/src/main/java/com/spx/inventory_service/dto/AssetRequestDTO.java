package com.spx.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@NoArgsConstructor
public class AssetRequestDTO implements Serializable {

    @NotBlank(message = "Serial number cannot be blank")
    private String serialNumber;

    private LocalDate purchaseDate;

    @NotNull(message = "Asset Type ID is required")
    private String assetTypeName;

    @NotNull(message = "Office ID is required")
    private String officeName;
}
