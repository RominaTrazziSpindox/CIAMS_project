package com.spx.inventory_management.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AssetTypeRequestDTO implements Serializable {

    @NotBlank(message = "Asset Type name cannot be blank")
    @Size(max = 100, message = "Asset Type name must not exceed 100 characters")
    private String assetTypeName;

    @Size(max = 200, message = "Asset Type description must not exceed 100 characters")
    private String assetTypeDescription;
}