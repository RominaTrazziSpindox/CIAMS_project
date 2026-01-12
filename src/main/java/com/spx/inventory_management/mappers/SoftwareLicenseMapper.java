package com.spx.inventory_management.mappers;
import com.spx.inventory_management.dto.SoftwareLicenseRequestDTO;
import com.spx.inventory_management.dto.SoftwareLicenseResponseDTO;
import com.spx.inventory_management.models.SoftwareLicense;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SoftwareLicenseMapper {

    // Request → Entity
    SoftwareLicense toEntity(SoftwareLicenseRequestDTO dto);

    // Entity → Response
    SoftwareLicenseResponseDTO toDTO(SoftwareLicense entity);

}

