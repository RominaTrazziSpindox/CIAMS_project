package com.spx.inventory_management.services;

import com.spx.inventory_management.models.Office;
import com.spx.inventory_management.repositories.OfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepository;

    // CRUD METHODS FROM SPRING DATA JPA

    // Retrieve all offices
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    // Retrieve an office by its id
    public Office getOfficeById(long id) {
        return officeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Office not found"));
    }

    // Insert a new office object into "offices" database table
    @Transactional
    public Office newOffice(Office newOffice) {
        return officeRepository.save(newOffice);
    }

    // Update an existing office
    @Transactional
    public Office updateExistingOffice(long id, Office updatedOffice) {

        // Find the office by its id (return: Optional<Object>)
        Office existingOffice = officeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Office not found"));

        // Set the new office name with the getter method
        existingOffice.setName(updatedOffice.getName());

        return officeRepository.save(existingOffice);
    }

    // Delete an office by its id
    @Transactional
    public void deleteOfficeById(long id) {

        // Check if the office exists (return: boolean)
        if (!officeRepository.existsById(id)) {
            throw new EntityNotFoundException("Office not found");
        }
        officeRepository.deleteById(id);
    }

}
