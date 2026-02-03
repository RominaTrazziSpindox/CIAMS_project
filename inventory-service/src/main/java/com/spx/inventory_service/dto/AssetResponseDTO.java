package com.spx.inventory_service.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AssetResponseDTO implements Serializable {

    private Long id;
    private String serialNumber;
    private LocalDate purchaseDate;

    private String officeName;
    private String assetTypeName;
}