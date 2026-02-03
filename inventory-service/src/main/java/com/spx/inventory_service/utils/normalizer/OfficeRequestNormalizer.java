package com.spx.inventory_service.utils.normalizer;

import com.spx.inventory_service.dto.OfficeRequestDTO;
import com.spx.inventory_service.utils.TextNormalizer;

/**
 * Normalizes OfficeRequestDTO fields.
 *
 * This class is responsible ONLY for preparing incoming DTO data.
 * No validation, no persistence, no business logic.
 */

public class OfficeRequestNormalizer {

    // Constructor
    private OfficeRequestNormalizer() {
    }


    // Method for fields normalization
    public static OfficeRequestDTO normalize(OfficeRequestDTO inputOfficeRequestDTO) {

        if (inputOfficeRequestDTO == null) {
            return null;
        }

        OfficeRequestDTO normalized = new OfficeRequestDTO();

        // Use of TextNormalizer function
        normalized.setOfficeName(
                TextNormalizer.normalizeKey(inputOfficeRequestDTO.getOfficeName())
        );

        return normalized;
    }
}