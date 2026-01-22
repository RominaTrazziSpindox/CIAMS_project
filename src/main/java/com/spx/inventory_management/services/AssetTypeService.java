package com.spx.inventory_management.services;

import com.spx.inventory_management.dto.AssetTypeRequestDTO;
import com.spx.inventory_management.dto.AssetTypeResponseDTO;
import com.spx.inventory_management.mappers.AssetTypeMapper;
import com.spx.inventory_management.models.AssetType;
import com.spx.inventory_management.repositories.AssetTypeRepository;
import com.spx.inventory_management.utils.normalizer.AssetTypeRequestNormalizer;
import com.spx.inventory_management.utils.TextNormalizer;
import com.spx.inventory_management.utils.validator.ReadValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@Slf4j
public class AssetTypeService {

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private ReadValidator readValidator;

    @Autowired
    private AssetTypeMapper assetTypeMapper;

    // ==========================================================
    // CRUD METHODS - From Repository Layer
    // ==========================================================

    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    /**
     * Gets all asset types.
     *
     * @return the all asset types
     */
    @Cacheable("assets-types")
    public List<AssetTypeResponseDTO> getAllAssetTypes() {

        log.info("Service getAllAssetTypes");

        return assetTypeRepository.findAll()
                .stream()
                .map(assetTypeMapper::toDTO)
                .toList();
    }

    /**
     * Gets asset type by name.
     *
     * @param assetTypeName the asset type name
     * @return the asset type by name
     */
    @Cacheable(value = "assets-types", key = "T(com.spx.inventory_management.utils.TextNormalizer).normalizeKey(#assetTypeName)")
    public AssetTypeResponseDTO getAssetTypeByName(String assetTypeName) {

        // Step 1: Check if the input Office entity is found and validate its name
        AssetType assetType = readValidator.checkIfEntityIsFound("AssetType", assetTypeName, assetTypeRepository::findByAssetTypeNameIgnoreCase);

        log.info("Service getAssetTypesByName");

        // Step 3: Mapper converts the entity into a DTO for response.
        return assetTypeMapper.toDTO(assetType);
    }

    // ==========================================================
    // CREATE OPERATION
    // ==========================================================

    /**
     * Create asset type asset type response dto.
     *
     * @param newAssetTypeRequestDTO the new asset type request dto
     * @return the asset type response dto
     */
    @Transactional
    public AssetTypeResponseDTO createAssetType(AssetTypeRequestDTO newAssetTypeRequestDTO) {

        // Step 1: Normalize all the input field incoming from assetTypeRequestDTO
        AssetTypeRequestDTO normalizedDTO = AssetTypeRequestNormalizer.normalize(newAssetTypeRequestDTO);

        // Step 2: Check if the asset type name already exists
        if (assetTypeRepository.existsByAssetTypeNameIgnoreCase(normalizedDTO.getAssetTypeName())) {
            throw new IllegalArgumentException("Asset type already exists: " + normalizedDTO.getAssetTypeName());
        }

        // Step 3. Convert DTO -> Entity (Database added an id automatically)
        AssetType newAssetType = assetTypeMapper.toEntity(normalizedDTO);

        // Step 4. Save the entity into the database
        AssetType saved = assetTypeRepository.save(newAssetType);

        log.info("AssetType created. name: {}", saved.getAssetTypeName());

        // Step 5. Convert Entity -> DTO
        return assetTypeMapper.toDTO(saved);
    }

    // ==========================================================
    // UPDATE OPERATION
    // ==========================================================

    /**
     * Update asset type by name asset type response dto.
     *
     * @param currentName     the current name
     * @param newAssetTypeDTO the new asset type dto
     * @return the asset type response dto
     */
    @Transactional
    public AssetTypeResponseDTO updateAssetTypeByName(String currentName, AssetTypeRequestDTO newAssetTypeDTO) {

        // Step 1: Normalize the current name
        String normalizedCurrentName = TextNormalizer.normalizeKey(currentName);

        // Step 2: Normalize incoming new asset type data
        AssetTypeRequestDTO normalizedDTO = AssetTypeRequestNormalizer.normalize(newAssetTypeDTO);

        // Step 3: Retrieve the existing asset type or throw if not found.
        AssetType existingAssetType = assetTypeRepository.findByAssetTypeNameIgnoreCase(normalizedCurrentName).orElseThrow(() -> {
            log.error("Update failed. AssetType not found. Name: {}", normalizedCurrentName);
            return new EntityNotFoundException("Asset type not found");
        });

        // Step 4: Extract the new asset type name
        String newName = normalizedDTO.getAssetTypeName();

        // Step 5: If the newName IS NOT EQUAL to the currentName AND if the newName already exists into the database...
        if (!normalizedCurrentName.equalsIgnoreCase(newName) && assetTypeRepository.existsByAssetTypeNameIgnoreCase(newName)) {
            throw new IllegalArgumentException("Asset type already exists: " + newName);
        }

        // Step 6: Update only mutable fields (in this case: asset type name and asset type description).
        existingAssetType.setAssetTypeName(newName);
        existingAssetType.setAssetTypeDescription(normalizedDTO.getAssetTypeDescription());

        // Step 7: Save the new updated Asset Type into the database
        AssetType updated = assetTypeRepository.save(existingAssetType);

        log.info("AssetType updated. oldName: {}, newName: {}", normalizedCurrentName, newName);


        // Step 8: Convert Entity -> DTO
        return assetTypeMapper.toDTO(updated);
    }

    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    /**
     * Delete asset type by name.
     *
     * @param assetTypeName the asset type name
     */
    @Transactional
    public void deleteAssetTypeByName(String assetTypeName) {

        // Step 1: Normalize the asset type name
        String normalizedName = TextNormalizer.normalizeKey(assetTypeName);

        // Step 2: Validate entity existence before deletion.
        if (!assetTypeRepository.existsByAssetTypeNameIgnoreCase(normalizedName)) {
            log.error("Delete failed. AssetType not found. Name: {}", normalizedName);
            throw new EntityNotFoundException("Asset type not found");
        }

        // Step 3: Proceed with deletion.
        assetTypeRepository.deleteByAssetTypeNameIgnoreCase(normalizedName);

        log.info("AssetType deleted. name={}", normalizedName);
    }
}