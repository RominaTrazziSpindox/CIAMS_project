package com.spx.inventory_management.services;

import com.spx.inventory_management.models.SoftwareLicense;
import com.spx.inventory_management.repositories.SoftwareLicenseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SoftwareLicenseService {

    @Autowired
    private SoftwareLicenseRepository softwareLicenseRepository;

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
}