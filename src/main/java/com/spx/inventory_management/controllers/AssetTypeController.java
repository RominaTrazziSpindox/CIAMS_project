package com.spx.inventory_management.controllers;

import com.spx.inventory_management.dto.AssetTypeRequestDTO;
import com.spx.inventory_management.dto.AssetTypeResponseDTO;
import com.spx.inventory_management.mapper.AssetTypeMapper;
import com.spx.inventory_management.models.AssetType;
import com.spx.inventory_management.services.AssetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * REST Controller responsible for managing AssetType-related operations.
 *
 * This controller exposes CRUD endpoints for the "asset types" resource.
 * It uses DTOs to isolate API contracts from internal JPA entities.
 * The mapping between DTOs and entities is handled by MapStruct.
 */
@RestController
@RequestMapping("/asset_types")
public class AssetTypeController {

    @Autowired
    public AssetTypeService assetTypeService;

    @Autowired
    public AssetTypeMapper mapper;

    // ==========================================================
    // CRUD METHODS - Service Layer Integration
    // ==========================================================

    /**
     * Gets all asset type dto.
     *
     * @return the all asset type dto
     */
    @GetMapping("/all")
    public List<AssetTypeResponseDTO> getAllAssetType() {

        List <AssetTypeResponseDTO> assetTypeResponseDTOS = assetTypeService.getAllAssetTypes()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return assetTypeResponseDTOS;
    }

    /**
     * Gets asset type dto by id.
     *
     * @param id the id
     * @return the asset type dto by id
     */
    @GetMapping("/{id}")
    public AssetTypeResponseDTO getAssetTypeById(@PathVariable long id) {

        // Step 1: Service retrieves an Asset Type entity by its unique ID.
        AssetType retrievedAssetType = assetTypeService.getAssetTypeById(id);

        // Step 2: Mapper converts the entity into a DTO for response.
        AssetTypeResponseDTO assetTypeResponseDTO = mapper.toDTO(retrievedAssetType);

        return assetTypeResponseDTO;
    }

    /**
     * Create asset type asset type response dto.
     *
     * @param newAssetTypeDTO the new asset type dto
     * @return the asset type response dto
     */
    @PostMapping("/insert")
    public AssetTypeResponseDTO createAssetType(@RequestBody AssetTypeRequestDTO newAssetTypeDTO) {

        // Step 1: Convert incoming DTO to an Entity for persistence.
        AssetType newAssetType = mapper.toEntity(newAssetTypeDTO);

        // Step 2: Persist the entity via service layer.
        AssetType savedAssetType = assetTypeService.createAssetType(newAssetType);

        // Step 3: Convert the persisted entity (with generated ID) back to a DTO.
        AssetTypeResponseDTO assetTypeResponseDTO = mapper.toDTO(savedAssetType);

        return assetTypeResponseDTO;
    }

    /**
     * Update asset type asset type response dto.
     *
     * @param id                  the id
     * @param updatedAssetTypeDTO the updated asset type dto
     * @return the asset type response dto
     */
    @PutMapping("/update/{id}")
    public AssetTypeResponseDTO updateAssetType(@PathVariable long id, @RequestBody AssetTypeRequestDTO updatedAssetTypeDTO) {

        // Step 1: Convert incoming DTO into an Entity containing updated values.
        AssetType updatedData = mapper.toEntity(updatedAssetTypeDTO);

        // Step 2: Delegate to service layer validation, update fields, and persist changes.
        AssetType updatedAssetType = assetTypeService.updateExistingAssetType(id, updatedData);

        // Step 3: Convert the updated entity into a response DTO.
        AssetTypeResponseDTO assetTypeResponseDTO = mapper.toDTO(updatedAssetType);

        return assetTypeResponseDTO;
    }

    /**
     * Delete asset type by id.
     *
     * @param id the id
     */
    @DeleteMapping("/{id}")
    public void deleteAssetTypeById(@PathVariable long id) {

        // Delegate to service layer for deletion logic.
       assetTypeService.deleteAssetTypeById(id);
    }
}
