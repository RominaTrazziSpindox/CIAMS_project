package com.spx.inventory_management.controllers;

import com.spx.inventory_management.dto.SoftwareLicenseRequestDTO;
import com.spx.inventory_management.dto.SoftwareLicenseResponseDTO;

import com.spx.inventory_management.services.SoftwareLicenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/software-licenses")
public class SoftwareLicenseController {


    @Autowired
    SoftwareLicenseService softwareLicenseService;



    // ==========================================================
    // CRUD METHODS - From Service Layer
    // ==========================================================

    // ==========================================================
    // READ
    // ==========================================================

    /**
     * Get all software licenses response entity.
     *
     * @return the response entity
     */
    @GetMapping("/all")
    public ResponseEntity<List<SoftwareLicenseResponseDTO>>getAllSoftwareLicenses() {

        // Step 1: Service try to retrieve an Asset list.
        List<SoftwareLicenseResponseDTO> licenses = softwareLicenseService.getAllSoftwareLicenses();

        // If the list is empty add a header with message
        if (licenses.isEmpty()) {
            return ResponseEntity.ok().header("X-Info-Message","No software licenses have been found in the database").body(licenses);

        }

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(licenses);
    }

    /**
     * Gets software license by name.
     *
     * @param softwareLicenseName the software license name
     * @return the software license by name
     */
    @GetMapping("/{softwareName}")
    public ResponseEntity<SoftwareLicenseResponseDTO>
    getSoftwareLicenseByName(@PathVariable String softwareLicenseName) {

        // Step 1: Service try to retrieve a Software license entity by its unique name.
        SoftwareLicenseResponseDTO license = softwareLicenseService.getSoftwareLicenseByName(softwareLicenseName);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(license);
    }



    // ==========================================================
    // CREATE
    // ==========================================================

    /**
     * Create software license response entity.
     *
     * @param newSoftwareLicense the new software license
     * @return the response entity
     */
    @PostMapping("/insert")
    public ResponseEntity<SoftwareLicenseResponseDTO> createSoftwareLicense(@Valid @RequestBody SoftwareLicenseRequestDTO newSoftwareLicense) {

        // Step 1: Persist the entity via service layer.
        SoftwareLicenseResponseDTO created = softwareLicenseService.createSoftwareLicense(newSoftwareLicense);

        // Step 2: return a 201 HTTP Status code
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ==========================================================
    // UPDATE
    // ==========================================================

    /**
     * Update software license response entity.
     *
     * @param softwareName       the software name
     * @param newSoftwareLicense the new software license
     * @return the response entity
     */
    @PutMapping("/update/{softwareName}")
    public ResponseEntity<SoftwareLicenseResponseDTO> updateSoftwareLicense(@PathVariable String softwareName, @Valid @RequestBody SoftwareLicenseRequestDTO newSoftwareLicense) {

        // Step 1: Delegate to service layer validation, update fields, and persist changes.
        SoftwareLicenseResponseDTO updated = softwareLicenseService.updateSoftwareLicense(softwareName, newSoftwareLicense);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(updated);
    }

    // ==========================================================
    // DELETE
    // ==========================================================

    /**
     * Delete software license response entity.
     *
     * @param softwareName the software name
     * @return the response entity
     */
    @DeleteMapping("/{softwareName}")
    public ResponseEntity<Void>
    deleteSoftwareLicense(@PathVariable String softwareName) {

        // Step 1: Delegate to service layer for deletion logic.
        softwareLicenseService.deleteSoftwareLicenseByName(softwareName);

        // Step 2: return a 204 HTTP Status code
        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // OTHER METHODS
    // ==========================================================

    // ==========================================================
    // SOFTWARE INSTALLATION & COMPLIANCE
    // ==========================================================

    /**
     * Install software license on asset response entity.
     *
     * @param softwareName the software name
     * @param serialNumber the serial number
     * @return the response entity
     */
    @PostMapping("/{softwareName}/install/{serialNumber}")
    public ResponseEntity<SoftwareLicenseResponseDTO> installSoftwareLicenseOnAsset(@PathVariable String softwareName, @PathVariable String serialNumber) {

        // Step 1: Delegate installation logic to the service layer.
        SoftwareLicenseResponseDTO installedSoftwareLicense = softwareLicenseService.installSoftwareLicenseOnAsset(softwareName, serialNumber);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(installedSoftwareLicense);
    }

    /**
     * Uninstall software license from asset response entity.
     *
     * @param softwareName the software name
     * @param serialNumber the serial number
     * @return the response entity
     */
    @DeleteMapping("/{softwareName}/uninstall/{serialNumber}")
    public ResponseEntity<SoftwareLicenseResponseDTO> uninstallSoftwareLicenseFromAsset(@PathVariable String softwareName,@PathVariable String serialNumber) {

        // Step 1: Delegate uninstallation logic to the service layer.
        SoftwareLicenseResponseDTO updated = softwareLicenseService.uninstallSoftwareLicenseFromAsset(softwareName, serialNumber);

        // Step 2: return a 204 HTTP Status code
        return ResponseEntity.noContent().build();

    }

    /**
     * Gets installed software license by asset.
     *
     * @param serialNumber the serial number
     * @return the installed software license by asset
     */
    // ==========================================================
    // AUDIT & QUERY
    // ==========================================================
    @GetMapping("/asset/{serialNumber}")
    public ResponseEntity<List<SoftwareLicenseResponseDTO>> getInstalledSoftwareLicenseByAsset(@PathVariable String serialNumber) {

        // Step 1: Service try to retrieve all the software licence owned by a specific asset (through its serial number)
        List<SoftwareLicenseResponseDTO>  installedLicensesOnAsset = softwareLicenseService.getInstalledSoftwareLicenseByAsset(serialNumber);

        // If the list is empty add a header with message
        if (installedLicensesOnAsset.isEmpty()) {
            return ResponseEntity.ok().header("X-Info-Message","No software licenses installed on this asset").body(installedLicensesOnAsset);
        }

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(installedLicensesOnAsset);
    }

    /**
     * Gets software licenses expiring soon.
     *
     * @return the software licenses expiring soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<SoftwareLicenseResponseDTO>> getSoftwareLicensesExpiringSoon() {

        // Step 1: Service try to retrieve a software licence list based on their expired dates.
        List<SoftwareLicenseResponseDTO> licenses = softwareLicenseService.getSoftwareLicensesExpiringSoon();

        // If the list is empty add a header with message
        if (licenses.isEmpty()) {
            return ResponseEntity.ok().header( "X-Info-Message","No software licenses expiring soon").body(licenses);
        }

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(licenses);
    }
}