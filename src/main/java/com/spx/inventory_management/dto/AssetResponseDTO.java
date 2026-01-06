package com.spx.inventory_management.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AssetResponseDTO implements Serializable {

    private Long id;
    private String serialNumber;
    private LocalDate purchaseDate;

    // From Office
    private String officeName;

    // From AssetType
    private String assetTypeName;
    private String assetTypeDescription;
}
