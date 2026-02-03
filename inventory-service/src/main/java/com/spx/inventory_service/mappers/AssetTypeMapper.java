package com.spx.inventory_service.mappers;
import com.spx.inventory_service.dto.AssetTypeRequestDTO;
import com.spx.inventory_service.dto.AssetTypeResponseDTO;
import com.spx.inventory_service.models.AssetType;

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
