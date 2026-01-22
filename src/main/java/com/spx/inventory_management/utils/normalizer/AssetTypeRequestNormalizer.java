package com.spx.inventory_management.utils.normalizer;

import com.spx.inventory_management.dto.AssetTypeRequestDTO;
import com.spx.inventory_management.utils.TextNormalizer;

/**
 * Normalizes OfficeRequestDTO fields.
 *
 * This class is responsible ONLY for preparing incoming DTO data.
 * No validation, no persistence, no business logic.
 *
 * Normalization rules:
 * - assetTypeName: normalized as a key (trimmed, single spaces, lowercase)
 * - assetTypeDescription: normalized as plain text (trimmed, single spaces)
 */
public class AssetTypeRequestNormalizer {

    // Constructor
    private AssetTypeRequestNormalizer() {

    }

    // Method for fields normalization
    public static AssetTypeRequestDTO normalize(AssetTypeRequestDTO inputAssetTypeRequestDTO) {

        if (inputAssetTypeRequestDTO == null) {
            return null;
        }

        AssetTypeRequestDTO normalized = new AssetTypeRequestDTO();

        // Normalize mandatory key field (using TextNormalizer function)
        normalized.setAssetTypeName(
                TextNormalizer.normalizeKey(inputAssetTypeRequestDTO.getAssetTypeName())
        );

        // Normalize optional descriptive field (using TextNormalizer function)
        normalized.setAssetTypeDescription(
                TextNormalizer.normalizeText(inputAssetTypeRequestDTO.getAssetTypeDescription())
        );

        return normalized;
    }
}