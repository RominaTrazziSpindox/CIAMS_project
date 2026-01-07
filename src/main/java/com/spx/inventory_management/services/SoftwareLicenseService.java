package com.spx.inventory_management.services;

import com.spx.inventory_management.models.Asset;
import com.spx.inventory_management.models.SoftwareLicense;
import com.spx.inventory_management.repositories.AssetRepository;
import com.spx.inventory_management.repositories.SoftwareLicenseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SoftwareLicenseService {

    @Autowired
    private SoftwareLicenseRepository softwareLicenseRepository;

    @Autowired
    private AssetRepository assetRepository;

    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    public List<SoftwareLicense> getAllSoftwareLicenses() {
        return softwareLicenseRepository.findAll();
    }

    public SoftwareLicense getSoftwareLicenseById(long id) {
        return softwareLicenseRepository.findById(id).orElseThrow(() -> {
            log.error("SoftwareLicense not found. id={}", id);
            return new EntityNotFoundException("Software License not found");
        });
    }

    // ==========================================================
    // CREATE OPERATION
    // ==========================================================
    @Transactional
    public SoftwareLicense createSoftwareLicense(SoftwareLicense newSoftwareLicense) {

        if (softwareLicenseRepository.existsBySoftwareName(newSoftwareLicense.getSoftwareName())) {
            log.error("Create failed. Software License already exists. name={}", newSoftwareLicense.getSoftwareName());
            throw new IllegalArgumentException("Software License already exists");
        }

        log.info("Creating Software License. name:'{}', maxInstallations:{}, expirationDate:{}",
                newSoftwareLicense.getSoftwareName(),
                newSoftwareLicense.getMaxInstallations(),
                newSoftwareLicense.getExpirationDate()
        );

        return softwareLicenseRepository.save(newSoftwareLicense);
    }

    // ==========================================================
    // UPDATE OPERATION
    // ==========================================================

    @Transactional
    public SoftwareLicense updateExistingSoftwareLicense(long id, SoftwareLicense updatedSoftwareLicense) {

        SoftwareLicense existingSoftwareLicense = softwareLicenseRepository.findById(id).orElseThrow(() -> {
            log.error("Update failed. Software License not found. id={}", id);
            return new EntityNotFoundException("Software License not found");
        });

        // Update only mutable fields (in this case: name, expiration date and number of maximum license).
        existingSoftwareLicense.setSoftwareName(updatedSoftwareLicense.getSoftwareName());
        existingSoftwareLicense.setMaxInstallations(updatedSoftwareLicense.getMaxInstallations());
        existingSoftwareLicense.setExpirationDate(updatedSoftwareLicense.getExpirationDate());

        log.info(
                "Updating Software License. id:{}, name:'{}', maxInstallations:{}, expirationDate:{}",
                id,
                updatedSoftwareLicense.getSoftwareName(),
                updatedSoftwareLicense.getMaxInstallations(),
                updatedSoftwareLicense.getExpirationDate()
        );

        return softwareLicenseRepository.save(existingSoftwareLicense);
    }

    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    @Transactional
    public void deleteSoftwareLicenseById(long id) {

        // Validate entity existence before deletion.
        if (!softwareLicenseRepository.existsById(id)) {
            log.error("Delete failed. Software License not found. id={}", id);
            throw new EntityNotFoundException("Software License not found");
        }

        // Proceed with deletion.
        log.info("Deleting Software License. id={}", id);
        softwareLicenseRepository.deleteById(id);
    }

    // ==========================================================
    // SOFTWARE INSTALLATION & COMPLIANCE
    // ==========================================================

    @Transactional
    public SoftwareLicense installationSoftware(long licenseId, long assetId) {

        // Check if the asset exists
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> {
            log.error("Installation failed. Asset not found. id={}", assetId);
            return new EntityNotFoundException("Asset not found");
        });

        // Check if the software license exists
        SoftwareLicense softwareLicense = softwareLicenseRepository.findById(licenseId).orElseThrow(() -> {
            log.error("Installation failed. Software License not found. id={}", licenseId);
            return new EntityNotFoundException("Software License not found");
        });

        // Check if the software license is not expired
        if (softwareLicense.getExpirationDate().isBefore(LocalDate.now())) {
            log.error("Installation failed. Software License expired. id={}", licenseId);
            throw new IllegalStateException("Software License is expired");
        }

        // Check if the software has already this license installed (= same licenseId)
        if (softwareLicense.getInstalledAssets().contains(asset)) {
            log.error("Installation failed. License already installed. licenseId={}, assetId={}", licenseId, assetId);
            throw new IllegalStateException("Software already installed on this asset");
        }

        // Check the number of installation
        if (softwareLicense.getMaxInstallations() != null && softwareLicense.getInstalledAssets().size() >= softwareLicense.getMaxInstallations()) {
            log.error("Installation failed. Number of maximum installations reached. licenseId={}", licenseId);
            throw new IllegalStateException("Maximum number of installations reached!");
        }

        // Add the license to the asset
        softwareLicense.getInstalledAssets().add(asset);

        log.info("The Software License has been installed. licenseId={}, assetId={}", licenseId, assetId);

        return softwareLicenseRepository.save(softwareLicense);
    }

    @Transactional
    public SoftwareLicense uninstallSoftware(long licenseId, long assetId) {

        // Check if the asset exists
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> {
            log.error("Uninstallation failed. Asset not found. id={}", assetId);
            return new EntityNotFoundException("Asset not found");
        });

        // Check if the software license exists
        SoftwareLicense softwareLicense = softwareLicenseRepository.findById(licenseId).orElseThrow(() -> {
            log.error("Uninstallation failed. Software License not found. id={}", licenseId);
            return new EntityNotFoundException("Software License not found");
        });

        // Check if the asset contains the specific software license
        if (!softwareLicense.getInstalledAssets().contains(asset)) {
            log.error("Uninstallatopm failed. Software not installed. licenseId={}, assetId={}",licenseId, assetId);
            throw new IllegalStateException("This Software license is not installed on this asset");
        }

        // Remove the license to the asset
        softwareLicense.getInstalledAssets().remove(asset);

        log.info("Uninstalled Software License. licenseId={}, assetId={}", licenseId, assetId);

        return softwareLicenseRepository.save(softwareLicense);
    }

    // Retrieve all the licenses owned by an asset
    public Set<SoftwareLicense> getInstalledSoftwareByAsset(long assetId) {

        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> {
            log.error("Audit failed. Asset not found. id={}", assetId);
            return new EntityNotFoundException("Asset not found");
        });

        log.info("Audit software for Asset. assetId={}", assetId);

        return asset.getSoftwareLicenses();
    }

    // Retrieve a list of the licenses that will be expired into 30 days
    public List<SoftwareLicense> getLicensesExpiringSoon() {

        LocalDate now = LocalDate.now();
        LocalDate limit = now.plusDays(30);

        log.info("Fetching software licenses expiring between {} and {}", now, limit);

        return softwareLicenseRepository.findByExpirationDateBetween(now, limit);
    }
}