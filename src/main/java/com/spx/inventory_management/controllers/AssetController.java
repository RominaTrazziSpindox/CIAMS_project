package com.spx.inventory_management.controllers;

import com.spx.inventory_management.dto.AssetRequestDTO;
import com.spx.inventory_management.dto.AssetResponseDTO;
import com.spx.inventory_management.mappers.AssetMapper;
import com.spx.inventory_management.models.Asset;
import com.spx.inventory_management.services.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    AssetService assetService;

    @Autowired
    AssetMapper mapper;


    // ==========================================================
    // CRUD METHODS - Service Layer
    // ==========================================================

    // ==========================================================
    // READ
    // ==========================================================

    /**
     * Gets all assets.
     *
     * @return the all assets
     */
    @GetMapping("/all")
    public List<AssetResponseDTO> getAllAssets() {

        List <AssetResponseDTO> assetResponseDTOS = assetService.getAllAssets()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return assetResponseDTOS;
    }

    /**
     * Gets asset by id.
     *
     * @param id the id
     * @return the asset by id
     */
    @GetMapping("/{id}")
    public AssetResponseDTO getAssetById(@PathVariable long id) {

        // Step 1: Service retrieves an Asset Type entity by its unique ID.
        Asset retrievedAsset = assetService.getAssetById(id);

        // Step 2: Mapper converts the entity into a DTO for response.
        AssetResponseDTO assetResponseDTO = mapper.toDTO(retrievedAsset);

        return assetResponseDTO;
    }

    /**
     * Gets asset by serial number.
     *
     * @param serialNumber the serial number
     * @return the asset by serial number
     */
    @GetMapping("/serial/{serialNumber}")
    public AssetResponseDTO getAssetBySerialNumber(@PathVariable String serialNumber) {

        // Step 1: Service retrieves an Asset Type entity by its unique Serial Number.
        Asset retrievedAsset = assetService.getAssetBySerialNumber(serialNumber);

        // Step 2: Mapper converts the entity into a DTO for response.
        AssetResponseDTO assetResponseDTO = mapper.toDTO(retrievedAsset);

        return assetResponseDTO;
    }

    // ==========================================================
    // CREATE
    // ==========================================================

    /**
     * Create asset asset response dto.
     *
     * @param dto the dto
     * @return the asset response dto
     */
    @PostMapping("/insert")
    public AssetResponseDTO createAsset(@RequestBody @Valid AssetRequestDTO dto) {

        // Step 1: Convert incoming DTO to an Entity for persistence.
        Asset asset = mapper.toEntity(dto);

        // Step 2: Persist the entity via service layer.
        Asset savedAsset = assetService.createAsset(
                asset,
                dto.getAssetTypeId(),
                dto.getOfficeId()
        );

        // Step 3: Convert the persisted entity (with generated ID) back to a DTO.
        AssetResponseDTO assetResponseDTO = mapper.toDTO(savedAsset);

        return assetResponseDTO;
    }

    // ==========================================================
    // UPDATE (asset fields only)
    // ==========================================================

    /**
     * Update asset asset response dto.
     *
     * @param id  the id
     * @param dto the dto
     * @return the asset response dto
     */
    @PutMapping("/update/{id}")
    public AssetResponseDTO updateAsset(@PathVariable long id, @RequestBody @Valid AssetRequestDTO dto) {

        // Step 1: Convert incoming DTO into an Entity containing updated values.
        Asset updatedData = mapper.toEntity(dto);

        // Step 2: Delegate to service layer validation, update fields, and persist changes.
        Asset updatedAsset = assetService.updateExistingAsset(id, updatedData);

        // Step 3: Convert the updated entity into a response DTO.
        AssetResponseDTO assetResponseDTO = mapper.toDTO(updatedAsset);

        return assetResponseDTO;
    }

    // ==========================================================
    // MOVE (office)
    // ==========================================================


    /**
     * Move asset to office asset response dto.
     *
     * @param assetId  the asset id
     * @param officeId the office id
     * @return the asset response dto
     */

    // http://localhost:8080/assets/move/1?officeId=2
    @PutMapping("/move/{assetId}")
    public AssetResponseDTO moveAssetToOffice(@PathVariable long assetId, @RequestParam long officeId) {

        // Step 1: Delegate to service layer to move the asset to a new office (by ID).
        Asset movedAsset = assetService.moveAssetToOfficeById(assetId, officeId);

        // Step 2: Convert the updated entity into a response DTO.
        AssetResponseDTO assetResponseDTO = mapper.toDTO(movedAsset);

        return assetResponseDTO;
    }

    /**
     * Move asset to office by name asset response dto.
     *
     * @param assetId       the name
     * @param officeName the office name
     * @return the asset response dto
     */

    // http://localhost:8080/assets/move-name/1?officeName=Rome HQ
    @PutMapping("/move-name/{assetId}")
    public AssetResponseDTO moveAssetToOfficeByName(@PathVariable long assetId, @RequestParam String officeName) {

        // Step 1: Delegate to service layer to move the asset from its current office to the target office identified by name.
        Asset movedAsset = assetService.moveAssetToOfficeByName(assetId, officeName);

        // Step 2: Convert the updated entity into a response DTO.
        AssetResponseDTO assetResponseDTO = mapper.toDTO(movedAsset);

        return assetResponseDTO;
    }

    // ==========================================================
    // DELETE
    // ==========================================================

    /**
     * Delete asset by id.
     *
     * @param id the id
     */
    @DeleteMapping("/{id}")
    public void deleteAssetById(@PathVariable long id) {

        // Delegate to service layer for deletion logic.
        assetService.deleteAssetById(id);
    }

    /**
     * Delete asset by serial number.
     *
     * @param serialNumber the serial number
     */
    @DeleteMapping("/serial/{serialNumber}")
    public void deleteAssetBySerialNumber(@PathVariable String serialNumber) {

        // Delegate to service layer for deletion logic.
        assetService.deleteAssetBySerialNumber(serialNumber);
    }
}