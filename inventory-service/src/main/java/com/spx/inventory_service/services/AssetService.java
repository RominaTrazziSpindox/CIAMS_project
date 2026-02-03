package com.spx.inventory_service.services;

import com.spx.inventory_service.dto.*;
import com.spx.inventory_service.mappers.AssetMapper;
import com.spx.inventory_service.models.Asset;
import com.spx.inventory_service.models.AssetType;
import com.spx.inventory_service.models.Office;
import com.spx.inventory_service.repositories.AssetRepository;
import com.spx.inventory_service.repositories.AssetTypeRepository;
import com.spx.inventory_service.repositories.OfficeRepository;
import com.spx.inventory_service.utils.normalizer.AssetRequestNormalizer;
import com.spx.inventory_service.utils.TextNormalizer;
import com.spx.inventory_service.utils.validator.CreateValidator;
import com.spx.inventory_service.utils.validator.ReadValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private ReadValidator readValidator;

    @Autowired
    private CreateValidator createValidator;

    @Autowired
    private AssetMapper assetMapper;


    // ==========================================================
    // CRUD METHODS - From Repository Layer
    // ==========================================================


    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    /**
     * Gets all assets.
     *
     * @return the all assets
     */
    public List<AssetResponseDTO> getAllAssets() {

        return assetRepository.findAll().stream().map(assetMapper::toDTO).toList();
    }

    /**
     * Gets assets by office.
     *
     * @param officeName the office name
     * @return the assets by office
     */
    public List<AssetResponseDTO> getAssetsByOffice(String officeName) {

        String normalizedOffice = TextNormalizer.normalizeKey(officeName);

        return assetRepository.findByOffice_NameIgnoreCase(normalizedOffice).stream().map(assetMapper::toDTO).toList();

    }

    /**
     * Gets assets by asset type.
     *
     * @param assetTypeName the asset type name
     * @return the assets by asset type
     */
    public List<AssetResponseDTO> getAssetsByAssetType(String assetTypeName) {

        // Step 1: Normalized the incoming asset serial number
        String normalizedTypeName = TextNormalizer.normalizeKey(assetTypeName);

        return assetRepository.findByAssetType_AssetTypeNameIgnoreCase(normalizedTypeName)
                .stream()
                .map(assetMapper::toDTO)
                .toList();
    }

    /**
     * Gets asset by serial number.
     *
     * @param assetSerialNumber the asset serial number
     * @return the asset by serial number
     */
    public AssetResponseDTO getAssetBySerialNumber(String assetSerialNumber) {

        // Step 1: Check if the input Office entity is found and validate its name
        Asset asset = readValidator.checkIfEntityIsFound("Asset", assetSerialNumber, assetRepository::findBySerialNumberIgnoreCase);

        // Step 2: Mapper converts the entity into a DTO for response.
        return assetMapper.toDTO(asset);
    }

    /**
     * Gets asset details by serial number.
     *
     * @param serialNumber the serial number
     * @return the asset details by serial number
     */
    public AssetDetailedResponseDTO getAssetDetailsBySerialNumber(String serialNumber) {

        // Step 1: Normalized the incoming asset serial number
        String normalizedSerialNumber = TextNormalizer.normalizeKey(serialNumber);

        // Step 2: Retrieve asset (with relations already available via JPA)
        Asset asset = assetRepository.findBySerialNumberIgnoreCase(normalizedSerialNumber).orElseThrow(() -> {
            log.error("Asset not found. This serial number doesn't exists: {}", normalizedSerialNumber);
            return new EntityNotFoundException("Asset not found" + normalizedSerialNumber);
        });

        // Step 3: Mapper converts the entity into a DTO for response.
        return assetMapper.toDetailedDTO(asset);
    }

    // ==========================================================
    // CREATE OPERATION
    // ==========================================================

    /**
     * Create asset asset response dto.
     *
     * @param newAssetRequestDTO the new asset request dto
     * @return the asset response dto
     */
    @Transactional
    public AssetResponseDTO createAsset(AssetRequestDTO newAssetRequestDTO) {

        // Step 1. Check if the input Asset entity already exists and validate its fields
        AssetRequestDTO normalizedDTO = createValidator.checkIfEntityAlreadyExists("Asset Type", newAssetRequestDTO,
                dto -> assetRepository.existsBySerialNumberIgnoreCase(dto.getSerialNumber()), AssetRequestNormalizer::normalize);


        // Step 2: Check if the office exists by its name [DA AGGIORNARE CON VALIDATOR]
        Office office = officeRepository.findByNameIgnoreCase(normalizedDTO.getOfficeName()).orElseThrow(() ->
                new EntityNotFoundException("Office not found"));

        // Step 3: Check if the asset type exists by its name [DA AGGIORNARE CON VALIDATOR]
        AssetType assetType = assetTypeRepository.findByAssetTypeNameIgnoreCase(normalizedDTO.getAssetTypeName()).orElseThrow(()
                -> new EntityNotFoundException("Asset type not found"));

        // Step 4. Convert DTO -> Entity (Database added an id automatically)
        Asset newAsset = assetMapper.toEntity(normalizedDTO);

        // Step 5: Set the new Asset foreign keys
        newAsset.setOffice(office);
        newAsset.setAssetType(assetType);

        // Step 6. Save the entity into the database
        Asset savedAsset = assetRepository.save(newAsset);

        log.info("Asset created succeffully. Serial number: {}", savedAsset.getSerialNumber());

        // Step 7. Convert Entity -> DTO
        return assetMapper.toDTO(savedAsset);
    }


    // ==========================================================
    // UPDATE OPERATIONS
    // ==========================================================

    /**
     * Update asset by serial number asset response dto.
     *
     * @param currentSerialNumber the current serial number
     * @param newAssetDTO         the new asset dto
     * @return the asset response dto
     */
    @Transactional
    public AssetResponseDTO updateAssetBySerialNumber(String currentSerialNumber, AssetRequestDTO newAssetDTO) {

        // Step 1: Normalize the current serial number
        String normalizedCurrentSerialNumber = TextNormalizer.normalizeKey(currentSerialNumber);

        // Step 2: Normalize incoming new asset type data
        AssetRequestDTO normalizedDTO = AssetRequestNormalizer.normalize(newAssetDTO);

        // Step 3: Retrieve the existing asset or throw if not found.
        Asset existingAsset = assetRepository.findBySerialNumberIgnoreCase(normalizedCurrentSerialNumber).orElseThrow(() -> {
                log.error("Update failed. Asset not updated, serial number not found. Serial number: {}", normalizedCurrentSerialNumber);
                return new EntityNotFoundException("Asset not found");
        });


        // Step 4: Extract the new asset serial number
        String newSerialNumber = normalizedDTO.getSerialNumber();

        // Step 5: If the newSerialNumber IS NOT EQUAL to the currentSerialNumber AND if the newSerialNumber already exists into the database...
        if (!normalizedCurrentSerialNumber.equalsIgnoreCase(newSerialNumber) && assetRepository.existsBySerialNumberIgnoreCase(newSerialNumber)) {
            throw new IllegalArgumentException( "Asset with serial number already exists: " + newSerialNumber);
        }

        // Step 6: Check if there is no office
        Office office = officeRepository.findByNameIgnoreCase(normalizedDTO.getOfficeName()).orElseThrow(() ->
                new EntityNotFoundException("Office not found"));

        // Step 7: Check if there is no asset type
        AssetType assetType = assetTypeRepository.findByAssetTypeNameIgnoreCase(normalizedDTO.getAssetTypeName()).orElseThrow(() ->
                new EntityNotFoundException("Asset type not found"));

        // Step 8: Set the foreign keys and update the mutable field (in this case: asset serial number, asset type and asset purchase date)
        existingAsset.setSerialNumber(newSerialNumber);
        existingAsset.setOffice(office);
        existingAsset.setAssetType(assetType);
        existingAsset.setPurchaseDate(normalizedDTO.getPurchaseDate());

        // Step 9: Save the new updated Asset into the database
        Asset updatedAsset = assetRepository.save(existingAsset);

        log.info( "Asset updated. OldSerial={}, NewSerial={}", normalizedCurrentSerialNumber, newSerialNumber);

        // Step 10: Convert Entity -> DTO
        return assetMapper.toDTO(updatedAsset);
    }


    /**
     * Move asset to office by name asset response dto.
     *
     * @param serialNumber      the serial number
     * @param updatedOfficeName the updated office name
     * @return the asset response dto
     */
    @Transactional
    public AssetResponseDTO moveAssetToOfficeByName( String serialNumber,String updatedOfficeName) {

        // Step 1: Normalize the current serial number
        String normalizedSerial = TextNormalizer.normalizeKey(serialNumber);

        // Step 2: Normalize incoming new asset type data
        String normalizedOfficeName = TextNormalizer.normalizeKey(updatedOfficeName);

        // Step 3: Retrieve the existing asset by serial number
        Asset asset = assetRepository.findBySerialNumberIgnoreCase(normalizedSerial).orElseThrow(() ->
                new EntityNotFoundException("Asset not found"));

        // Step 4: Retrieve the target office by its name
        Office targetOffice = officeRepository.findByNameIgnoreCase(normalizedOfficeName).orElseThrow(() ->
                new EntityNotFoundException("Office not found"));

        // Step 5: Check if asset is already in that office
        if (asset.getOffice() != null && asset.getOffice().getName().equalsIgnoreCase(normalizedOfficeName)) {
            throw new IllegalArgumentException("Asset is already assigned to office '" + normalizedOfficeName + "'");
        }

        // Step 6: Move asset by setting the new office
        asset.setOffice(targetOffice);

        // Step 7: Save the new office data into the database
        Asset savedOffice = assetRepository.save(asset);

        log.info("Asset moved. Serial: {}, NewOffice: {}", normalizedSerial, normalizedOfficeName);


        // Step 8: Entity -> DTO
        return assetMapper.toDTO(savedOffice);
    }

    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    /**
     * Delete asset by serial number.
     *
     * @param serialNumber the serial number
     */
    @Transactional
    public void deleteAssetBySerialNumber(String serialNumber) {

        // Step 1: Normalize the serial number input field incoming from assetRequestDTO
        String normalizedSerialNumber = TextNormalizer.normalizeKey(serialNumber);

        // Step 2: Validate entity existence before deletion.
        if (!assetRepository.existsBySerialNumberIgnoreCase(normalizedSerialNumber)) {
            log.error("Delete failed. Asset not found. Serial number: {}", normalizedSerialNumber);
            throw new EntityNotFoundException("Asset not found");
        }

        assetRepository.deleteBySerialNumberIgnoreCase(normalizedSerialNumber);

        log.info("Asset deleted. Serial: {}", normalizedSerialNumber);
    }
}











































