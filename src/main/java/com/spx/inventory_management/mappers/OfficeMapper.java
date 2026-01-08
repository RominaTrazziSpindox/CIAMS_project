package com.spx.inventory_management.mappers;

import com.spx.inventory_management.dto.OfficeRequestDTO;
import com.spx.inventory_management.dto.OfficeResponseDTO;
import com.spx.inventory_management.models.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OfficeMapper {

    // DTO → Entity
    @Mapping(target = "id", ignore = true) // because client doesn't send the ID
    @Mapping(target = "name", source = "officeName")
    Office toEntity(OfficeRequestDTO dto);

    // Entity → DTO
    @Mapping(target = "officeName", source = "name")
    OfficeResponseDTO toDTO(Office entity);
}