package com.spx.inventory_management.services;

import com.spx.inventory_management.models.AssetType;
import com.spx.inventory_management.repositories.AssetTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.spx.inventory_management.utils.TextNormalizer;

import java.util.List;

@Service
@Slf4j
public class AssetTypeService {

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    /**
     * Gets all asset types.
     *
     * @return the all asset types
     */
    @Cacheable("asset_types")
    public List<AssetType> getAllAssetTypes() {
        return assetTypeRepository.findAll();
    }

    /**
     * Gets asset type by id.
     *
     * @param id the id
     * @return the asset type by id
     */
    @Cacheable(value = "asset_types", key = "#id")
    public AssetType getAssetTypeById(long id) {
        return assetTypeRepository.findById(id).orElseThrow(() -> {
            log.error("AssetType not found. id={}", id);
            return new EntityNotFoundException("AssetType not found");
        });
    }


    // ==========================================================
    // CREATE OPERATION
    // ==========================================================

    /**
     * Create asset type.
     *
     * @param newAssetType the new asset type
     * @return the asset type
     */
    @Transactional
    @CacheEvict(value = "asset_types", allEntries = true)
    public AssetType createAssetType(AssetType newAssetType) {

        // Cleaning incoming assetType name data using utility function
        String newAssetTypeName = TextNormalizer.normalizeKey(newAssetType.getAssetTypeName());
        newAssetType.setAssetTypeName(newAssetTypeName);

        // Check if the office name already exists
        if (assetTypeRepository.existsByAssetTypeName(newAssetTypeName)) {
            throw new IllegalArgumentException("Asset type already exists: " + newAssetTypeName);
        }

        log.info("Creating a new Asset Type. name:'{}' and description:'{}'", newAssetType.getAssetTypeName(), newAssetType.getAssetTypeDescription());
        return assetTypeRepository.save(newAssetType);
    }

    // ==========================================================
    // UPDATE OPERATION
    // ==========================================================

    /**
     * Update existing asset type.
     *
     * @param id               the id
     * @param updatedAssetType the updated asset type
     * @return the asset type
     */
    @Transactional
    @CacheEvict(value = "asset_types", allEntries = true)
    public AssetType updateExistingAssetType(long id, AssetType updatedAssetType) {

        // Cleaning incoming assetType name data
        String newAssetTypeName = TextNormalizer.normalizeKey(updatedAssetType.getAssetTypeName());

        // Retrieve the existing office or throw if not found.
        AssetType existingAssetType = assetTypeRepository.findById(id).orElseThrow(() -> {
            log.error("Update failed. Asset Type not found. id={}", id);
            return new EntityNotFoundException("Asset Type not found");
        });

        // Get the current AssetType name
        String currentOfficeName = existingAssetType.getAssetTypeName();

        // Update only mutable fields (in this case: name and description).
        existingAssetType.setAssetTypeName(updatedAssetType.getAssetTypeName());
        existingAssetType.setAssetTypeDescription(updatedAssetType.getAssetTypeDescription());

        // Save and return the updated entity.
        log.info("Updating Asset Type. id:{}, name:'{}', description:'{}'",
                id, updatedAssetType.getAssetTypeName(), updatedAssetType.getAssetTypeDescription());

        return assetTypeRepository.save(existingAssetType);
    }


    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    /**
     * Delete asset type by id.
     *
     * @param id the id
     */
    @Transactional
    @CacheEvict(value = "asset_types", allEntries = true)
    public void deleteAssetTypeById(long id) {

        // Validate entity existence before deletion.
        if (!assetTypeRepository.existsById(id)) {
            log.error("Delete failed. Asset Type not found. id={}", id);
            throw new EntityNotFoundException("Asset Type not found");
        }

        // Proceed with deletion.
        log.info("Deleting Asset Type. id={}", id);
        assetTypeRepository.deleteById(id);
    }
}
