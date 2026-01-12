package com.spx.inventory_management.controllers;

import com.spx.inventory_management.dto.AssetTypeRequestDTO;
import com.spx.inventory_management.dto.AssetTypeResponseDTO;
import com.spx.inventory_management.services.AssetTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/asset-types")
public class AssetTypeController {

    @Autowired
    public AssetTypeService assetTypeService;



    // ==========================================================
    // CRUD METHODS - From Service Layer
    // ==========================================================

    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    @GetMapping("/all")
    public ResponseEntity<List<AssetTypeResponseDTO>> getAllAssetTypes() {

        // Step 1: Service try to retrieve an Asset Type list.
        List<AssetTypeResponseDTO> assetTypes = assetTypeService.getAllAssetTypes();

        // If the list is empty add a header with message
        if (assetTypes.isEmpty()) {
            return ResponseEntity.ok().header("X-Info-Message", "No asset types found in the database").body(assetTypes);
        }

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(assetTypes);
    }

    @GetMapping("/{name}")
    public ResponseEntity<AssetTypeResponseDTO> getAssetTypeByName(@PathVariable String name) {

        // Step 1: Service try to retrieve an Office entity by its unique name.
        AssetTypeResponseDTO assetType = assetTypeService.getAssetTypeByName(name);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(assetType);
    }

    // ==========================================================
    // CREATE OPERATION
    // ==========================================================

    @PostMapping("/insert")
    public ResponseEntity<AssetTypeResponseDTO> createAssetType(@Valid @RequestBody AssetTypeRequestDTO assetTypeRequestDTO) {

        // Step 1: Persist the entity via service layer.
        AssetTypeResponseDTO created = assetTypeService.createAssetType(assetTypeRequestDTO);

        // Step 2: return a 201 HTTP Status code
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ==========================================================
    // UPDATE OPERATION
    // ==========================================================

    @PutMapping("/update/{name}")
    public ResponseEntity<AssetTypeResponseDTO> updateAssetTypeByName(@PathVariable String name, @Valid @RequestBody AssetTypeRequestDTO assetTypeRequestDTO) {

        // Step 1: Delegate to service layer validation, update fields, and persist changes.
        AssetTypeResponseDTO updated = assetTypeService.updateAssetTypeByName(name, assetTypeRequestDTO);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(updated);
    }

    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteAssetTypeByName(@PathVariable String name) {

        // Step 1: Delegate to service layer for deletion logic.
        assetTypeService.deleteAssetTypeByName(name);

        // Step 2: return a 204 HTTP Status code
        return ResponseEntity.noContent().build();
    }
}
