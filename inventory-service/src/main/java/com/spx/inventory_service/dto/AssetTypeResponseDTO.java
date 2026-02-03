package com.spx.inventory_service.dto;

import lombok.*;

import java.io.Serializable;

@Data
public class AssetTypeResponseDTO implements Serializable {

    private Long id;
    private String assetTypeName;
    private String assetTypeDescription;
}
