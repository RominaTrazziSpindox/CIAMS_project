package com.spx.inventory_management.mappers;

import com.spx.inventory_management.dto.AssetDetailedResponseDTO;
import com.spx.inventory_management.dto.AssetRequestDTO;
import com.spx.inventory_management.dto.AssetResponseDTO;
import com.spx.inventory_management.models.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    // ==========================================================
    // DTO -> Entity (solo campi semplici)
    // ==========================================================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "office", ignore = true)
    @Mapping(target = "assetType", ignore = true)
    @Mapping(target = "softwareLicenses", ignore = true)
    Asset toEntity(AssetRequestDTO dto);

    // ==========================================================
    // Entity -> DTO (BASE)
    // ==========================================================

    @Mapping(source = "office.name", target = "officeName")
    @Mapping(source = "assetType.assetTypeName", target = "assetTypeName")
    AssetResponseDTO toDTO(Asset asset);

    // ==========================================================
    // Entity -> DTO (DETAILED)
    // ==========================================================

    @Mapping(source = "office.name", target = "office.officeName")
    AssetDetailedResponseDTO toDetailedDTO(Asset asset);
}