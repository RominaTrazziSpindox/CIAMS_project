package com.spx.inventory_management.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Data
public class AssetDetailedResponseDTO implements Serializable {

    private String serialNumber;
    private LocalDate purchaseDate;

    private OfficeResponseDTO office;
    private AssetTypeResponseDTO assetType;

    @JsonIgnoreProperties({"installedAssets"})
    private Set<SoftwareLicenseResponseDTO> softwareLicenses;
}
