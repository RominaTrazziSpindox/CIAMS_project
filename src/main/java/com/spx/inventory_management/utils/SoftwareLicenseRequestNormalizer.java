package com.spx.inventory_management.utils;

import com.spx.inventory_management.dto.SoftwareLicenseRequestDTO;

public class SoftwareLicenseRequestNormalizer {

    // Constructor
    private SoftwareLicenseRequestNormalizer() {
    }

    public static SoftwareLicenseRequestDTO normalize(SoftwareLicenseRequestDTO inputSoftwareLicenceDTO) {

        if (inputSoftwareLicenceDTO == null) {
            return null;
        }

        SoftwareLicenseRequestDTO normalized = new SoftwareLicenseRequestDTO();

        // Normalize human key
        normalized.setSoftwareName(
                TextNormalizer.normalizeKey(inputSoftwareLicenceDTO.getSoftwareName())
        );

        // Copy non-text fields as-is
        normalized.setExpirationDate(inputSoftwareLicenceDTO.getExpirationDate());
        normalized.setMaxInstallations(inputSoftwareLicenceDTO.getMaxInstallations());

        return normalized;
    }
}