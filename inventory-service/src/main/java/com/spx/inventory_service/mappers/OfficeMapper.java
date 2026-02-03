package com.spx.inventory_service.mappers;

import com.spx.inventory_service.dto.OfficeRequestDTO;
import com.spx.inventory_service.dto.OfficeResponseDTO;
import com.spx.inventory_service.models.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OfficeMapper {

    // DTO → Entity
    @Mapping(target = "id", ignore = true) // because client doesn't send the ID
    @Mapping(target = "name", source = "officeName") // target: db column, source: dto property
    Office toEntity(OfficeRequestDTO dto);

    // Entity → DTO
    @Mapping(target = "officeName", source = "name") // target; dto property, source: db column
    OfficeResponseDTO toDTO(Office entity);
}