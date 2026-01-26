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

    private String officeName;
    private String assetTypeName;
}