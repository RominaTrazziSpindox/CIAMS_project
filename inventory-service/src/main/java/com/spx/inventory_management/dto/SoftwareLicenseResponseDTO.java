package com.spx.inventory_management.dto;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Data
public class SoftwareLicenseResponseDTO implements Serializable {

    private Long id;
    private String softwareName;
    private Integer maxInstallations;
    private LocalDate expirationDate;

    // Assets where this license is installed
    private Set<AssetResponseDTO> installedAssets;
}