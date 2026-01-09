package com.spx.inventory_management.services;

import com.spx.inventory_management.models.Office;
import com.spx.inventory_management.repositories.OfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.spx.inventory_management.utils.TextNormalizer;


import java.util.List;

/**
 * Service layer for managing {@link Office} entities.
 *
 * This class encapsulates the business logic related to "offices",
 * mediating between the Controller (which uses DTOs) and the Repository (which interacts with the database).
 *
 * Responsibilities:
 *
 * 1. Orchestrate CRUD operations provided by {@link OfficeRepository}</li>
 * 2. Handle transactional boundaries for data consistency</li>
 * 3. Manage logging and exception handling</li>
 *
 */
@Service
@Slf4j
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepository;

    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    /**
     * Retrieves all {@link Office} entities from the database.
     *
     * @return a list of all existing Office entities
     */
    @Cacheable("offices")
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    /**
     * Retrieves an {@link Office} entity by its ID.
     *
     * @param id the unique identifier of the office
     * @return the matching Office entity
     * @throws EntityNotFoundException if no entity with the given ID exists
     */
    @Cacheable(value = "offices", key = "#id")
    public Office getOfficeById(long id) {
        return officeRepository.findById(id).orElseThrow(() -> {
                    log.error("Office not found. id={}", id);
                    return new EntityNotFoundException("Office not found");
        });
    }

    // ==========================================================
    // CREATE OPERATION
    // ==========================================================

    /**
     * Persists a new {@link Office} entity in the database.
     *
     * This method is transactional, ensuring rollback in case of runtime exceptions.
     *
     * @param newOffice the new Office entity to be saved
     * @return the saved Office entity (including its generated ID)
     */
    @Transactional
    @CacheEvict(value = "offices", allEntries = true)
    public Office createOffice(Office newOffice) {

        // Cleaning incoming office name data using utility function
        String newOfficeName = TextNormalizer.normalizeKey(newOffice.getName());
        newOffice.setName(newOfficeName);

        // Check if the office name already exists
        if (officeRepository.existsByName(newOfficeName)) {
            throw new IllegalArgumentException("Office name already exists: " + newOfficeName);
        }

        log.info("Creating office. name='{}'", newOffice.getName());
        return officeRepository.save(newOffice);
    }

    // ==========================================================
    // UPDATE OPERATION
    // ==========================================================

    /**
     * Updates an existing {@link Office} entity.
     *
     * Finds the existing record, updates its mutable fields, and saves it back to the database.
     *
     * @param id             the ID of the office to update
     * @param updatedOffice  an Office object containing the new field values
     * @return the updated Office entity
     * @throws EntityNotFoundException if no entity with the given ID exists
     */
    @Transactional
    @CacheEvict(value = "offices", allEntries = true)
    public Office updateExistingOffice(long id, Office updatedOffice) {

        // Cleaning incoming office name data
        String newOfficeName = TextNormalizer.normalizeKey(updatedOffice.getName());

        // Retrieve the existing office or throw if not found.
        Office existingOffice = officeRepository.findById(id).orElseThrow(() -> {
                    log.error("Update failed. Office not found. id={}", id);
                    return new EntityNotFoundException("Office not found");
        });

        // Get the current office name
        String currentOfficeName = existingOffice.getName();

        // Check if the office name already exists
        if (!currentOfficeName.equals(newOfficeName) && officeRepository.existsByName(newOfficeName)) {
            throw new IllegalArgumentException("Office name already exists: " + newOfficeName);
        }

        // Update only mutable fields (in this case: office name).
        existingOffice.setName(updatedOffice.getName());

        // Save and return the updated entity.
        log.info("Updating office. id={}, newName='{}'", id, updatedOffice.getName());
        return officeRepository.save(existingOffice);
    }

    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    /**
     * Deletes an {@link Office} entity by its ID.
     *
     * @param id the ID of the office to delete
     * @throws EntityNotFoundException if no entity with the given ID exists
     */
    @Transactional
    @CacheEvict(value = "offices", key = "#id", allEntries = true)
    public void deleteOfficeById(long id) {

        // Validate entity existence before deletion.
        if (!officeRepository.existsById(id)) {
            log.error("Delete failed. Office not found. id={}", id);
            throw new EntityNotFoundException("Office not found");
        }

        // Proceed with deletion.
        log.info("Deleting office. id={}", id);
        officeRepository.deleteById(id);
    }
}