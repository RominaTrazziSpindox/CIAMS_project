package com.spx.inventory_management.mapper;

import com.spx.inventory_management.dto.AssetRequestDTO;
import com.spx.inventory_management.dto.AssetResponseDTO;
import com.spx.inventory_management.models.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "office", ignore = true)
    @Mapping(target = "assetType", ignore = true)
    Asset toEntity(AssetRequestDTO dto);


    // Entity -> DTO
    @Mapping(source = "office.name", target = "officeName")
    @Mapping(source = "assetType.assetTypeName", target = "assetTypeName")
    @Mapping(source = "assetType.assetTypeDescription", target = "assetTypeDescription")
    AssetResponseDTO toDTO(Asset asset);
}
