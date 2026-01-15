package com.spx.inventory_management.services;
import com.spx.inventory_management.dto.SoftwareLicenseRequestDTO;
import com.spx.inventory_management.dto.SoftwareLicenseResponseDTO;
import com.spx.inventory_management.mappers.SoftwareLicenseMapper;
import com.spx.inventory_management.models.Asset;
import com.spx.inventory_management.models.SoftwareLicense;
import com.spx.inventory_management.repositories.AssetRepository;
import com.spx.inventory_management.repositories.SoftwareLicenseRepository;
import com.spx.inventory_management.utils.SoftwareLicenseRequestNormalizer;
import com.spx.inventory_management.utils.TextNormalizer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@Slf4j
public class SoftwareLicenseService {

    @Autowired
    private SoftwareLicenseRepository softwareLicenseRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private SoftwareLicenseMapper softwareLicenseMapper;

    // ==========================================================
    // CRUD METHODS - From Repository Layer
    // ==========================================================


    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    /**
     * Gets all software licenses.
     *
     * @return the all software licenses
     */
    public List<SoftwareLicenseResponseDTO> getAllSoftwareLicenses() {
        return softwareLicenseRepository.findAll()
                .stream()
                .map(softwareLicenseMapper::toDTO)
                .toList();
    }

    /**
     * Gets software license by name.
     *
     * @param softwareName the software name
     * @return the software license by name
     */
    public SoftwareLicenseResponseDTO getSoftwareLicenseByName(String softwareName) {

        // Step 1: Normalized the incoming software name license
        String normalizedSoftwareName = TextNormalizer.normalizeKey(softwareName);

        // Step 2: Repository try to retrieve a SoftwareLicence entity by its unique name from the database.
        SoftwareLicense license = softwareLicenseRepository.findBySoftwareNameIgnoreCase(normalizedSoftwareName).orElseThrow(() -> {
            log.error("Asset not found. This software name doesn't exists: {}", normalizedSoftwareName);
            return new EntityNotFoundException("Software Licence not found " + normalizedSoftwareName);
        });

        // Step 3: Mapper converts the entity into a DTO for response.
        return softwareLicenseMapper.toDTO(license);
    }


    /**
     * Create software license response dto.
     *
     * @param newSoftwareLicenseRequestDTO dto
     * @return the software license response dto
     */
    // ==========================================================
    // CREATE OPERATION
    // ==========================================================
    @Transactional
    public SoftwareLicenseResponseDTO createSoftwareLicense(SoftwareLicenseRequestDTO newSoftwareLicenseRequestDTO) {

        // Step 1: Normalize all the input field incoming from SoftwareLicenceRequestDTO
        SoftwareLicenseRequestDTO normalizedDTO = SoftwareLicenseRequestNormalizer.normalize(newSoftwareLicenseRequestDTO);

        log.debug("Creating a new software license: {}", normalizedDTO.getSoftwareName());


        // Step 2: Check if the software license name already exists
        if (softwareLicenseRepository.existsBySoftwareNameIgnoreCase(normalizedDTO.getSoftwareName())) {
            throw new IllegalArgumentException("Software license already exists: " + normalizedDTO.getSoftwareName());
        }

        // Step 3. Convert DTO -> Entity (Database added an id automatically)
        SoftwareLicense newSoftwareLicenceEntity = softwareLicenseMapper.toEntity(normalizedDTO);

        // Step 4. Save the entity into the database
        SoftwareLicense savedSoftwareLicense = softwareLicenseRepository.save(newSoftwareLicenceEntity);

        log.info("Software license created: {}", savedSoftwareLicense.getSoftwareName());

        // Step 6. Convert Entity -> DTO
        return softwareLicenseMapper.toDTO(savedSoftwareLicense);
    }

    // ==========================================================
    // UPDATE OPERATION
    // ==========================================================

    /**
     * Update software license response dto.
     *
     * @param softwareName          the software name
     * @param newSoftwareLicenseDTO the new software license dto
     * @return the software license response dto
     */
    @Transactional
    public SoftwareLicenseResponseDTO updateSoftwareLicense(String softwareName, SoftwareLicenseRequestDTO newSoftwareLicenseDTO) {

        // Step 1: Normalize the current software license name
        String normalizedCurrentName = TextNormalizer.normalizeKey(softwareName);

        // Step 2: Normalize incoming new software license data
        SoftwareLicenseRequestDTO normalizedUpdated = SoftwareLicenseRequestNormalizer.normalize(newSoftwareLicenseDTO);

        // Step 3: Retrieve the existing software license or throw if not found.
        SoftwareLicense existingSoftwareLicense = softwareLicenseRepository.findBySoftwareNameIgnoreCase(normalizedCurrentName).orElseThrow(() ->
                new EntityNotFoundException("Software license not found"));

        // Step 4: Extract the new software license name
        String newSoftwareLicenseName = normalizedUpdated.getSoftwareName();

        // Step 5: If the newSoftwareLicenseName IS NOT EQUAL to the currentSoftwareLicenseName AND if the newSoftwareLicenseName already exists into the database...
        if (!normalizedCurrentName.equalsIgnoreCase(newSoftwareLicenseName) && softwareLicenseRepository.existsBySoftwareNameIgnoreCase(newSoftwareLicenseName)) {
            throw new IllegalArgumentException("Software license already exists: " + newSoftwareLicenseName);
        }

        // Step 6: Update only mutable fields (in this case: office name).
        existingSoftwareLicense.setSoftwareName(newSoftwareLicenseName);
        existingSoftwareLicense.setExpirationDate(normalizedUpdated.getExpirationDate());
        existingSoftwareLicense.setMaxInstallations(normalizedUpdated.getMaxInstallations());

        // Step 7: Save the new updated Software license into the database
        SoftwareLicense saved = softwareLicenseRepository.save(existingSoftwareLicense);

        log.info("Software license updated. OldName: {}, NewName: {}", normalizedCurrentName, newSoftwareLicenseName);

        // Step 8: Convert Entity -> DTO
        return softwareLicenseMapper.toDTO(saved);
    }


    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    /**
     * Delete software license by name.
     *
     * @param softwareName the software name
     */
    @Transactional
    public void deleteSoftwareLicenseByName(String softwareName) {

        // Step 1: Normalize the software license name
        String normalizedName = TextNormalizer.normalizeKey(softwareName);

        // Step 2: Validate entity existence before deletion.
        if (!softwareLicenseRepository.existsBySoftwareNameIgnoreCase(normalizedName)) {
            throw new EntityNotFoundException("Software license not found");
        }

        // Step 3: Proceed with deletion.
        softwareLicenseRepository.deleteBySoftwareNameIgnoreCase(normalizedName);


        log.info("Software license deleted: {}", normalizedName);

    }

    // ==========================================================
    // SOFTWARE INSTALLATION & COMPLIANCE
    // ==========================================================

    /**
     * Install software on asset software license response dto.
     *
     * @param softwareName the software name
     * @param serialNumber the serial number
     * @return the software license response dto
     */
    @Transactional
    public SoftwareLicenseResponseDTO installSoftwareLicenseOnAsset(String softwareName, String serialNumber) {

        // Step 1: Normalize inputs
        String normalizedSoftwareName = TextNormalizer.normalizeKey(softwareName);
        String normalizedSerialNumber = TextNormalizer.normalizeKey(serialNumber);

        // Step 2:  Check if the asset exists
        Asset asset = assetRepository.findBySerialNumberIgnoreCase(normalizedSerialNumber).orElseThrow(() -> {
            log.error("Installation failed. Asset not found. serial= {}", normalizedSerialNumber);
            return new EntityNotFoundException("Asset not found");
        });

        // Step 3: Check if the software license exists
        SoftwareLicense softwareLicense = softwareLicenseRepository.findBySoftwareNameIgnoreCase(normalizedSoftwareName).orElseThrow(() -> {
            log.error("Installation failed. Software license not found. Software license name: {}", normalizedSoftwareName);
            return new EntityNotFoundException("Software license not found");
        });

        // Step 4: Check if the software license is not expired
        if (softwareLicense.getExpirationDate() != null && softwareLicense.getExpirationDate().isBefore(LocalDate.now())) {
            log.error("Installation failed. Software license is expired. Software license name: {}", normalizedSoftwareName);
            throw new IllegalStateException("Software license is expired");
        }

        // Step 5: Check if already installed on an asset
        if (softwareLicense.getInstalledAssets().contains(asset)) {
            log.error("Installation failed. License already installed. Software license name: {}, Asset serial number: {}", normalizedSoftwareName, normalizedSerialNumber);
            throw new IllegalStateException("This software license is already installed on this asset");
        }

        // Step 6: Check the number of maximum installations
        if (softwareLicense.getMaxInstallations() != null && softwareLicense.getInstalledAssets().size() >= softwareLicense.getMaxInstallations()) {
            log.error("Installation failed. Max installations reached. Software: {}", normalizedSoftwareName);
            throw new IllegalStateException("Maximum number of installations reached");
        }

        // Step 8: Add the license to the asset (= installation)
        softwareLicense.addAsset(asset);

        // Step 9: Save the entity into the database
        SoftwareLicense savedSoftwareLicense = softwareLicenseRepository.save(softwareLicense);

        log.info("Software successfully installed. Software license name:{}, Asset serial number:{}", normalizedSoftwareName, normalizedSerialNumber);

        // Step 10: Convert Entity -> DTO
        return softwareLicenseMapper.toDTO(savedSoftwareLicense);
    }

    /**
     * Uninstall software from asset software license response dto.
     *
     * @param softwareName the software name
     * @param serialNumber the serial number
     * @return the software license response dto
     */
    @Transactional
    public SoftwareLicenseResponseDTO uninstallSoftwareLicenseFromAsset(String softwareName, String serialNumber) {

        // Step 1: Normalize inputs
        String normalizedSoftwareName = TextNormalizer.normalizeKey(softwareName);
        String normalizedSerialNumber = TextNormalizer.normalizeKey(serialNumber);

        // Step 2:  Check if the asset exists
        Asset asset = assetRepository.findBySerialNumberIgnoreCase(normalizedSerialNumber).orElseThrow(() -> {
            log.error("Installation failed. Asset not found. serial= {}", normalizedSerialNumber);
            return new EntityNotFoundException("Asset not found");
        });

        // Step 3: Check if the software license exists
        SoftwareLicense softwareLicense = softwareLicenseRepository.findBySoftwareNameIgnoreCase(normalizedSoftwareName).orElseThrow(() -> {
            log.error("Installation failed. Software license not found. Software license name: {}", normalizedSoftwareName);
            return new EntityNotFoundException("Software license not found");
        });

        // Step 4: Check if already installed on an asset
        if (!softwareLicense.getInstalledAssets().contains(asset)) {
            log.error("Uninstallation failed. Software not installed. Software license name: {}, Asset serial number: {}", normalizedSoftwareName, normalizedSerialNumber);
            throw new IllegalStateException("This software license is not installed on this asset");
        }

        // Step 5: Remove the license to the asset (= installation)
        softwareLicense.removeAsset(asset);

        // Step 6: Save the entity into the database
        SoftwareLicense savedSoftwareLicense = softwareLicenseRepository.save(softwareLicense);

        log.info("Software uninstalled. Software license name:{}, Asset serial number:{}", normalizedSoftwareName, normalizedSerialNumber);

        // Step 7: Convert Entity -> DTO
        return softwareLicenseMapper.toDTO(savedSoftwareLicense);

    }


    /**
     * Gets installed software by asset.
     *
     * @param serialNumber the serial number
     * @return the installed software by asset
     */
    // Retrieve all the licenses owned by an asset
    public List<SoftwareLicenseResponseDTO> getInstalledSoftwareLicenseBySerialNumber(String serialNumber) {

        // Step 1: Normalize the incoming asset serial number
        String normalizedSerialNumber = TextNormalizer.normalizeKey(serialNumber);

        log.info("Audit software for asset. Asset serial number: {}", normalizedSerialNumber);

        return softwareLicenseRepository
                .findByInstalledAssets_SerialNumberIgnoreCase(normalizedSerialNumber)
                .stream()
                .map(softwareLicenseMapper::toDTO)
                .toList();


    }

    /**
     * Gets licenses expiring soon.
     *
     * @return the licenses expiring soon
     */
    // Retrieve all the licenses that will expire in 30 days
    public List<SoftwareLicenseResponseDTO> getSoftwareLicensesExpiringSoon() {

        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(30);

        return softwareLicenseRepository.findByExpirationDateBetween(today, limit)
                .stream()
                .map(softwareLicenseMapper::toDTO)
                .toList();
    }
}

