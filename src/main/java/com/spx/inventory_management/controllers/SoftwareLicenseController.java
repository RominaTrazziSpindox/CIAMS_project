package com.spx.inventory_management.controllers;

import com.spx.inventory_management.dto.SoftwareLicenseRequestDTO;
import com.spx.inventory_management.dto.SoftwareLicenseResponseDTO;
import com.spx.inventory_management.mapper.SoftwareLicenseMapper;
import com.spx.inventory_management.models.SoftwareLicense;
import com.spx.inventory_management.services.SoftwareLicenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/software-licenses")
public class SoftwareLicenseController {

    @Autowired
    SoftwareLicenseService softwareLicenseService;

    @Autowired
    SoftwareLicenseMapper mapper;

    // ==========================================================
    // CRUD METHODS
    // ==========================================================

    // ==========================================================
    // READ
    // ==========================================================

    /**
     * Gets all software licenses.
     *
     * @return the list of software licenses
     */
    @GetMapping("/all")
    public List<SoftwareLicenseResponseDTO> getAllSoftwareLicenses() {

        List<SoftwareLicenseResponseDTO> responseDTOS =
                softwareLicenseService.getAllSoftwareLicenses()
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());

        return responseDTOS;
    }

    /**
     * Gets software license by id.
     *
     * @param id the license id
     * @return the software license
     */
    @GetMapping("/{id}")
    public SoftwareLicenseResponseDTO getSoftwareLicenseById(@PathVariable long id) {

        // Step 1: Service retrieves the Software License entity by its ID.
        SoftwareLicense retrievedSoftwareLicense = softwareLicenseService.getSoftwareLicenseById(id);

        // Step 2: Mapper converts the entity into a Response DTO.
        SoftwareLicenseResponseDTO responseDTO = mapper.toDto(retrievedSoftwareLicense);

        return responseDTO;
    }

    // ==========================================================
    // CREATE
    // ==========================================================

    /**
     * Create software license.
     *
     * @param dto the request dto
     * @return the software license response dto
     */
    @PostMapping("/insert")
    public SoftwareLicenseResponseDTO createSoftwareLicense(@RequestBody @Valid SoftwareLicenseRequestDTO dto
    ) {

        // Step 1: Convert incoming Request DTO to an Entity.
        SoftwareLicense newLicense = mapper.toEntity(dto);

        // Step 2: Delegate creation logic to the service layer.
        SoftwareLicense savedSoftwareLicense = softwareLicenseService.createSoftwareLicense(newLicense);

        // Step 3: Convert the persisted entity into a Response DTO.
        SoftwareLicenseResponseDTO responseDTO = mapper.toDto(savedSoftwareLicense);

        return responseDTO;
    }

    // ==========================================================
    // UPDATE
    // ==========================================================

    /**
     * Update existing software license.
     *
     * @param id  the license id
     * @param dto the request dto
     * @return the software license response dto
     */
    @PutMapping("/update/{id}")
    public SoftwareLicenseResponseDTO updateSoftwareLicense(@PathVariable long id, @RequestBody @Valid SoftwareLicenseRequestDTO dto) {

        // Step 1: Convert incoming DTO into an Entity containing updated values.
        SoftwareLicense updatedData = mapper.toEntity(dto);

        // Step 2: Delegate update logic to the service layer.
        SoftwareLicense updatedSoftwareLicense = softwareLicenseService.updateExistingSoftwareLicense(id, updatedData);

        // Step 3: Convert the updated entity into a Response DTO.
        SoftwareLicenseResponseDTO responseDTO = mapper.toDto(updatedSoftwareLicense);

        return responseDTO;
    }

    // ==========================================================
    // DELETE
    // ==========================================================

    /**
     * Delete software license by id.
     *
     * @param id the license id
     */
    @DeleteMapping("/{id}")
    public void deleteSoftwareLicenseById(@PathVariable long id) {

        // Delegate deletion logic to the service layer.
        softwareLicenseService.deleteSoftwareLicenseById(id);
    }

    // ==========================================================
    // OTHER METHODS
    // ==========================================================

    // ==========================================================
    // SOFTWARE INSTALLATION & COMPLIANCE
    // ==========================================================

    /**
     * Install a software license on a specific asset.
     *
     * @param licenseId the license id
     * @param assetId   the asset id
     * @return the updated software license response dto
     */
    @PostMapping("/{licenseId}/install/{assetId}")
    public SoftwareLicenseResponseDTO installSoftware(@PathVariable long licenseId, @PathVariable long assetId) {

        // Step 1: Delegate installation logic to the service layer.
        SoftwareLicense updatedSoftwareLicense = softwareLicenseService.installationSoftware(licenseId, assetId);

        // Step 2: Convert updated entity into a Response DTO.
        SoftwareLicenseResponseDTO responseDTO = mapper.toDto(updatedSoftwareLicense);

        return responseDTO;
    }

    /**
     * Uninstall a software license from a specific asset.
     *
     * @param licenseId the license id
     * @param assetId   the asset id
     * @return the updated software license response dto
     */
    @DeleteMapping("/{licenseId}/uninstall/{assetId}")
    public SoftwareLicenseResponseDTO uninstallSoftware(@PathVariable long licenseId, @PathVariable long assetId) {

        // Step 1: Delegate uninstallation logic to the service layer.
        SoftwareLicense updatedLicense = softwareLicenseService.uninstallSoftware(licenseId, assetId);

        // Step 2: Convert updated entity into a Response DTO.
        SoftwareLicenseResponseDTO responseDTO = mapper.toDto(updatedLicense);

        return responseDTO;
    }

    // ==========================================================
    // AUDIT & QUERY
    // ==========================================================

    /**
     * Retrieve all software licenses installed on a specific asset.
     *
     * @param assetId the asset id
     * @return the set of installed software licenses
     */
    @GetMapping("/asset/{assetId}")
    public Set<SoftwareLicenseResponseDTO> getInstalledSoftwareByAsset(@PathVariable long assetId) {

        Set<SoftwareLicenseResponseDTO> responseDTOS = softwareLicenseService.getInstalledSoftwareByAsset(assetId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toSet());

        return responseDTOS;
    }

    /**
     * Retrieve software licenses expiring in the next 30 days.
     *
     * @return the list of expiring software licenses
     */
    @GetMapping("/expiring-soon")
    public List<SoftwareLicenseResponseDTO> getLicensesExpiringSoon() {

        List<SoftwareLicenseResponseDTO> responseDTOS = softwareLicenseService.getLicensesExpiringSoon()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return responseDTOS;
    }
}



