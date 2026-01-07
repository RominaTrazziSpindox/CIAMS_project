package com.spx.inventory_management.services;

import com.spx.inventory_management.models.Asset;
import com.spx.inventory_management.models.AssetType;
import com.spx.inventory_management.models.Office;
import com.spx.inventory_management.repositories.AssetRepository;
import com.spx.inventory_management.repositories.AssetTypeRepository;
import com.spx.inventory_management.repositories.OfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The type Asset service.
 */
@Service
@Slf4j
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private OfficeRepository officeRepository;

    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    /**
     * Gets all assets.
     *
     * @return the all assets
     */
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    /**
     * Gets asset by id.
     *
     * @param id the id
     * @return the asset by id
     */
    public Asset getAssetById(long id) {
        return assetRepository.findById(id).orElseThrow(() -> {
            log.error("Asset not found. id={}", id);
            return new EntityNotFoundException("Asset not found");
        });
    }

    /**
     * Gets asset by serial number.
     *
     * @param serialNumber the serial number
     * @return the asset by serial number
     */
    public Asset getAssetBySerialNumber(String serialNumber) {

        // Find the Asset by serial number
        Optional<Asset> optionalAsset = assetRepository.findBySerialNumber(serialNumber);

        // If the Asset doesn't exist
        if (optionalAsset.isEmpty()) {
            log.error("Asset not found. This serial number doesn't exists={}", serialNumber);
            throw new EntityNotFoundException("Asset not found with serial number: " + serialNumber);
        }

        // If the Asset exists
        return optionalAsset.get();
    }


    // ==========================================================
    // CREATE OPERATION
    // ==========================================================

    /**
     * Create asset asset.
     *
     * @param asset       the asset
     * @param assetTypeId the asset type id
     * @param officeId    the office id
     * @return the asset
     */
    @Transactional
    public Asset createAsset(Asset asset, Long assetTypeId, Long officeId) {

        log.debug("Creating asset with serialNumber={}", asset.getSerialNumber());

        // 1. Check serial number uniqueness
        if (assetRepository.existsBySerialNumber(asset.getSerialNumber())) {
            throw new IllegalArgumentException(
                    "Asset with serial number already exists: " + asset.getSerialNumber()
            );
        }

        // 2. Check if AssetType exists
        AssetType assetType = assetTypeRepository.findById(assetTypeId).orElseThrow(() ->
                new EntityNotFoundException("AssetType not found with id=" + assetTypeId)
        );

        // 3. Check if the Office exists
        Office office = officeRepository.findById(officeId).orElseThrow(() ->
                new EntityNotFoundException("Office not found with id=" + officeId)
        );


        // 4. Set the foreign keys
        asset.setAssetType(assetType);
        asset.setOffice(office);

        // 5. Persist
        Asset savedAsset = assetRepository.save(asset);

        log.info("Asset created successfully. id={}", savedAsset.getId());

        return savedAsset;
    }


    // ==========================================================
    // UPDATE OPERATIONS
    // ==========================================================

    /**
     * Update existing asset asset.
     *
     * @param id           the id
     * @param updatedAsset the updated asset
     * @return the asset
     */
    @Transactional
    public Asset updateExistingAsset(long id, Asset updatedAsset) {

        // Retrieve the existing office or throw an exception if not found.
        Asset existingAsset = assetRepository.findById(id).orElseThrow(() -> {
            log.error("Update failed. Asset not found. id={}", id);
            return new EntityNotFoundException("Asset not found");
        });

        // Update the date of purchase field
        existingAsset.setPurchaseDate(updatedAsset.getPurchaseDate());

        // Update the serial number (checking unique constraint)
        if (!existingAsset.getSerialNumber().equals(updatedAsset.getSerialNumber())) {

            if (assetRepository.existsBySerialNumber(updatedAsset.getSerialNumber())) {
                throw new IllegalArgumentException( "Asset with serial number already exists: " + updatedAsset.getSerialNumber());
            }

            // Update the serial number field
            existingAsset.setSerialNumber(updatedAsset.getSerialNumber());
        }

        // Save and return the updated entity.
        log.info("Asset updated successfully. id={}", id);
        return assetRepository.save(existingAsset);
    }

    /**
     * Move asset to office by id asset.
     *
     * @param assetId         the asset id
     * @param updatedOfficeId the updated office id
     * @return the asset
     */

    // SQL: UPDATE assets SET id_office = ? WHERE id_asset = ?
    @Transactional
    public Asset moveAssetToOfficeById(long assetId, long updatedOfficeId) {

        // Try to retrieve an asset by its id
        Asset retrievedAsset = assetRepository.findById(assetId).orElseThrow(() -> {
            log.error("Move failed. Asset not found. id={}", assetId);
            return new EntityNotFoundException("Asset not found");
        });

        // Try to retrieve the target office by its id
        Office updatedOffice = officeRepository.findById(updatedOfficeId).orElseThrow(() -> {
            log.error("Move failed. Office not found. id={}", updatedOfficeId);
            return new EntityNotFoundException("Office not found");
        });

        // Check if the asset is already assigned to the target office
        if (retrievedAsset.getOffice().getId() == updatedOfficeId) {
            throw new IllegalArgumentException("Asset is already assigned to office id=" + updatedOfficeId);
        }

        // Update the office allocation
        retrievedAsset.setOffice(updatedOffice);

        // Persist the update
        Asset savedAsset = assetRepository.save(retrievedAsset);

        log.info("Asset {} moved to office {}", assetId, updatedOfficeId);

        return savedAsset;
    }


    /**
     * Move asset to office by name asset.
     *
     * @param assetId           the asset id
     * @param updatedOfficeName the updated office name
     * @return the asset
     */
    @Transactional
    public Asset moveAssetToOfficeByName(long assetId, String updatedOfficeName) {

        // Step 1: Retrieve the asset
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> {
            log.error("Move failed. Asset not found. id={}", assetId);
            return new EntityNotFoundException("Asset not found");
        });

        // Step 2: Retrieve the target office by name
        Office updatedOffice = officeRepository.findByName(updatedOfficeName).orElseThrow(() -> {
            log.error("Move failed. Office not found. name={}", updatedOfficeName);
            return new EntityNotFoundException("Office not found with name=" + updatedOfficeName);
        });

        // Step 3: Check if the asset is already in the target office
        Office currentOffice = asset.getOffice();
        if (currentOffice.getId() == updatedOffice.getId()) {
            throw new IllegalArgumentException("Asset is already assigned to office '" + updatedOfficeName + "'");
        }

        // Step 4: Move the asset into the new office (setting the new field property name)
        asset.setOffice(updatedOffice);

        // Step 5: Persist and return
        Asset savedAsset = assetRepository.save(asset);

        log.info("Asset {} moved from office '{}' to office '{}'", assetId, currentOffice.getName(), updatedOffice.getName());

        return savedAsset;
    }


    // ==========================================================
    // DELETE OPERATIONS
    // ==========================================================

    /**
     * Delete asset by id.
     *
     * @param id the id
     */
    @Transactional
    public void deleteAssetById(long id) {

        // Validate entity existence before deletion.
        if (!assetRepository.existsById(id)) {
            log.error("Delete failed. Asset not found. id={}", id);
            throw new EntityNotFoundException("Asset not found");
        }

        // Proceed with deletion.
        log.info("Deleting asset. id={}", id);
        assetRepository.deleteById(id);
    }

    /**
     * Delete asset by serial number.
     *
     * @param serialNumber the serial number
     */
    @Transactional
    public void deleteAssetBySerialNumber(String serialNumber) {

        // Validate entity existence before deletion.
        if (!assetRepository.existsBySerialNumber(serialNumber)) {
            log.error("Delete failed. Asset not found. Serial number={}", serialNumber);
            throw new EntityNotFoundException("Asset not found");
        }

        // Proceed with deletion.
        log.info("Deleting asset. Serial number={}", serialNumber);
        assetRepository.deleteBySerialNumber(serialNumber);

    }


}
