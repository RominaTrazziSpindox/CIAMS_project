package com.spx.inventory_management.mapper;
import com.spx.inventory_management.dto.AssetTypeRequestDTO;
import com.spx.inventory_management.dto.AssetTypeResponseDTO;
import com.spx.inventory_management.models.AssetType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetTypeMapper {

    // DTO → Entity
    @Mapping(target = "id", ignore = true) // because client doesn't send the ID
    AssetType toEntity(AssetTypeRequestDTO dto);

    // Entity → DTO
    AssetTypeResponseDTO toDTO(AssetType entity);

}
