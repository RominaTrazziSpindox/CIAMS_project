package com.spx.inventory_management.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

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

    // From SoftwareLicenses
    private Set<SoftwareLicenseResponseDTO> softwareLicenses;
}
