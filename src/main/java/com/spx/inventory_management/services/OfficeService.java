package com.spx.inventory_management.services;

import com.spx.inventory_management.models.Office;
import com.spx.inventory_management.repositories.OfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepository;

    // CRUD METHODS FROM JPA

    // Retrieve all the offices
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    // Retrieve an office by its id
    public Office getOfficeById(long id) {
        return officeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Office not found"));
    }

    // Insert a new office into the office list
    @Transactional
    public Office newOffice(Office newOffice) {
        return officeRepository.save(newOffice);
    }

    // Update an existing office into the office list
    @Transactional
    public Office updateExistingOffice(Office updatedOffice, long id) {

        // Find an existing office by its id, if it doesn't exist throw an error
        Office existingOffice = officeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Office not found"));

        // Set the new office name of the existing office (updating with the property of the parameter object)
        existingOffice.setName(updatedOffice.getName());

        return officeRepository.save(existingOffice);
    }

    // Delete an office by its id
    @Transactional
    public void deleteOfficeById(long id) {
        if (!officeRepository.existsById(id)) {
            throw new EntityNotFoundException("Office not found");
        }
        officeRepository.deleteById(id);
    }

}
