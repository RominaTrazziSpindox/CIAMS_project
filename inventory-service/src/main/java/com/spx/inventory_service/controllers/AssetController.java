package com.spx.inventory_service.controllers;

import com.spx.inventory_service.dto.AssetDetailedResponseDTO;
import com.spx.inventory_service.dto.AssetRequestDTO;
import com.spx.inventory_service.dto.AssetResponseDTO;
import com.spx.inventory_service.services.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    AssetService assetService;


    // ==========================================================
    // CRUD METHODS - From Service Layer
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
    public ResponseEntity<List<AssetResponseDTO>> getAllAssets() {

        // Step 1: Service try to retrieve an Asset list.
        List<AssetResponseDTO> assets = assetService.getAllAssets();

        // If the list is empty add a header with message
        if (assets.isEmpty()) {
            return ResponseEntity.ok().header("X-Info-Message", "No assets found in the database").body(assets);

        }

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets by office response entity.
     *
     * @param officeName the office name
     * @return the response entity
     *
     * URL: GET /assets/offices/Milan HQ
     */
    @GetMapping("/offices/{officeName}")
    public ResponseEntity<List<AssetResponseDTO>> getAssetsByOffice(@PathVariable String officeName){

        // Step 1: Service try to retrieve an Asset list based on an office name.
        List<AssetResponseDTO> assets = assetService.getAssetsByOffice(officeName);

        // If the list is empty add a header with message
        if (assets.isEmpty()) {
            return ResponseEntity.ok().header("X-Info-Message", "No assets found in the database").body(assets);

        }

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(assets);
    }

    /**
     * Gets assets by asset type.
     *
     * @param assetTypeName the asset type name
     * @return the assets by asset type
     *
     * URL: GET /assets/asset-types/Laptop
     *
     */
    @GetMapping("/asset-types/{assetTypeName}")
    public ResponseEntity<List<AssetResponseDTO>> getAssetsByAssetType(@PathVariable String assetTypeName) {

        List<AssetResponseDTO> assets = assetService.getAssetsByAssetType(assetTypeName);

        if (assets.isEmpty()) {
            return ResponseEntity.ok().header("X-Info-Message", "No assets found for the specified asset type").body(assets);
        }

        return ResponseEntity.ok(assets);
    }

    /**
     * Gets asset by serial number.
     *
     * @param serialNumber the serial number
     * @return the asset by serial number
     *
     * URL: GET /assets/MONXXX
     */
    @GetMapping("/{serialNumber}")
    public ResponseEntity<AssetResponseDTO> getAssetBySerialNumber(@PathVariable String serialNumber) {

        // Step 1: Service try to retrieve an Office entity by its unique serial number.
        AssetResponseDTO asset = assetService.getAssetBySerialNumber(serialNumber);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(asset);
    }


    /**
     * Gets asset details.
     *
     * @param serialNumber the serial number
     * @return the asset details
     *
     * URL: GET /assets/MONXXX/details
     */
    @GetMapping("/{serialNumber}/details")
    public ResponseEntity<AssetDetailedResponseDTO> getAssetDetails(@PathVariable String serialNumber) {

        // Step 1: Service try to retrieve an Office entity by its unique serial number.
        AssetDetailedResponseDTO assetDetails = assetService.getAssetDetailsBySerialNumber(serialNumber);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(assetDetails);
    }



    // ==========================================================
    // CREATE OPERATIONS
    // ==========================================================

    /**
     * Create asset response entity.
     *
     * @param assetRequestDTO the asset request dto
     * @return the response entity
     */
    @PostMapping("/insert")
    public ResponseEntity<AssetResponseDTO> createAsset(@Valid @RequestBody AssetRequestDTO assetRequestDTO) {

        // Step 1: Persist the entity via service layer.
        AssetResponseDTO createdAsset = assetService.createAsset(assetRequestDTO);

        // Step 2: return a 201 HTTP Status code
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAsset);
    }


    // ==========================================================
    // UPDATE OPERATIONS
    // ==========================================================

    /**
     * Update asset response entity.
     *
     * @param serialNumber    the serial number
     * @param updatedAssetDTO the updated asset dto
     * @return the response entity
     */
    @PutMapping("/update/{serialNumber}")
    public ResponseEntity<AssetResponseDTO> updateAsset(@PathVariable String serialNumber, @Valid @RequestBody AssetRequestDTO updatedAssetDTO) {

        // Step 1: Delegate to service layer validation, update fields, and persist changes.
        AssetResponseDTO updatedAsset = assetService.updateAssetBySerialNumber(serialNumber, updatedAssetDTO);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(updatedAsset);
    }


    // ==========================================================
    // MOVE (office)
    // ==========================================================

    /**
     * Move asset to office response entity.
     *
     * @param serialNumber  the serial number
     * @param newOfficeName the new office name
     * @return the response entity
     *
     * URL: PUT /assets/SN-001/move?newOfficeName=Rome HQ
     */
    @PutMapping("/{serialNumber}/move")
    public ResponseEntity<AssetResponseDTO> moveAssetToOffice(@PathVariable String serialNumber, @RequestParam String newOfficeName) {

        // Step 1: Service takes a serialNumber of an asset and a new office name
        AssetResponseDTO movedAsset = assetService.moveAssetToOfficeByName(serialNumber, newOfficeName);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(movedAsset);
    }

    // ==========================================================
    // DELETE
    // ==========================================================

    /**
     * Delete asset response entity.
     *
     * @param serialNumber the serial number
     * @return the response entity
     */
    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String serialNumber) {

        // Step 1: Delegate to service layer for deletion logic.
        assetService.deleteAssetBySerialNumber(serialNumber);

        // Step 2: return a 204 HTTP Status code
        return ResponseEntity.noContent().build();
    }
}