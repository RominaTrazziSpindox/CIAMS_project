package com.spx.inventory_management.utils.normalizer;

import com.spx.inventory_management.dto.AssetRequestDTO;
import com.spx.inventory_management.utils.TextNormalizer;

/**
 * Utility class responsible for normalizing incoming AssetRequestDTO data.
 *
 * Normalization rules:
 * - serialNumber: normalized as a key
 * - officeName: normalized as a key
 * - assetTypeName: normalized as a key
 * - purchaseDate: untouched
 */
public class AssetRequestNormalizer {

    // Constructor
    private AssetRequestNormalizer() {

    }

    // Method for fields normalization
    public static AssetRequestDTO normalize(AssetRequestDTO inputAssetRequestDTO) {

        if (inputAssetRequestDTO == null) {
            return null;
        }

        AssetRequestDTO normalized = new AssetRequestDTO();

        normalized.setSerialNumber(
                TextNormalizer.normalizeKey(inputAssetRequestDTO.getSerialNumber())
        );

        normalized.setOfficeName(
                TextNormalizer.normalizeKey(inputAssetRequestDTO.getOfficeName())
        );

        normalized.setAssetTypeName(
                TextNormalizer.normalizeKey(inputAssetRequestDTO.getAssetTypeName())
        );

        normalized.setPurchaseDate(
                inputAssetRequestDTO.getPurchaseDate()
        );

        return normalized;
    }
}
