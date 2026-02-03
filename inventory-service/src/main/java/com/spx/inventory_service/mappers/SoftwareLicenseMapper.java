package com.spx.inventory_service.mappers;
import com.spx.inventory_service.dto.SoftwareLicenseRequestDTO;
import com.spx.inventory_service.dto.SoftwareLicenseResponseDTO;
import com.spx.inventory_service.models.SoftwareLicense;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AssetMapper.class })
public interface SoftwareLicenseMapper {

    // Request → Entity
    SoftwareLicense toEntity(SoftwareLicenseRequestDTO dto);

    // Entity → Response
    SoftwareLicenseResponseDTO toDTO(SoftwareLicense entity);

}

