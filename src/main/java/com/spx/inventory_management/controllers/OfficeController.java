package com.spx.inventory_management.controllers;

import com.spx.inventory_management.dto.OfficeRequestDTO;
import com.spx.inventory_management.dto.OfficeResponseDTO;
import com.spx.inventory_management.mappers.OfficeMapper;
import com.spx.inventory_management.models.Office;
import com.spx.inventory_management.services.OfficeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Office controller.
 */
@RestController
@RequestMapping("/offices")
public class OfficeController {


    @Autowired
    public OfficeService officeService;

    // ==========================================================
    // CRUD METHODS - From Service Layer
    // ==========================================================

    // ==========================================================
    // READ OPERATIONS
    // ==========================================================

    /**
     * Gets all offices.
     *
     * @return the all offices
     */
    @GetMapping("/all")
    public ResponseEntity<List<OfficeResponseDTO>> getAllOffices() {


        // Step 1: Service try to retrieve an Office list.
        List<OfficeResponseDTO> officesRetrieved = officeService.getAllOffices();

        // If the list is empty...
        if (officesRetrieved.isEmpty()) {
            return ResponseEntity.ok().header("X-Info-Message", "No offices found in the database").body(officesRetrieved);
        }

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(officesRetrieved);
    }


    /**
     * Gets office by name.
     *
     * @param name the name
     * @return the office by name
     */
    @GetMapping("/{name}")
    public ResponseEntity<OfficeResponseDTO> getOfficeByName(@PathVariable String name) {

        // Step 1: Service try to retrieve an Office entity by its unique name.
        OfficeResponseDTO retrievedOffice = officeService.getOfficeByName(name);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(retrievedOffice);
    }


    // ==========================================================
    // CREATE OPERATION
    // ==========================================================

    /**
     * Create office response entity.
     *
     * @param newOfficeDTO the new office dto
     * @return the response entity
     */
    @PostMapping("/insert")
    public ResponseEntity<OfficeResponseDTO> createOffice(@Valid @RequestBody OfficeRequestDTO newOfficeDTO) {

        // Step 1: Persist the entity via service layer.
        OfficeResponseDTO savedOffice = officeService.createOffice(newOfficeDTO);

        // Step 2: return a 201 HTTP Status code
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOffice);
    }

    // ==========================================================
    // UPDATE OPERATION
    // ==========================================================

    /**
     * Update office by name response entity.
     *
     * @param name             the name
     * @param updatedOfficeDTO the updated office dto
     * @return the response entity
     */
    @PutMapping("/update/{name}")
    public ResponseEntity<OfficeResponseDTO> updateOfficeByName(@PathVariable String name, @Valid @RequestBody OfficeRequestDTO updatedOfficeDTO ) {

        // Step 1: Delegate to service layer validation, update fields, and persist changes.
        OfficeResponseDTO updatedOffice =  officeService.updateExistingOfficeByName(name, updatedOfficeDTO);

        // Step 2: return a 200 HTTP Status code
        return ResponseEntity.ok(updatedOffice);
    }


    // ==========================================================
    // DELETE OPERATION
    // ==========================================================

    /**
     * Delete office by id response entity.
     *
     * @param name the name
     * @return the response entity
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteOfficeById(@PathVariable String name) {

        // Step 1: Delegate to service layer for deletion logic.
        officeService.deleteOfficeByName(name);

        // Step 2: return a 204 HTTP Status code
        return ResponseEntity.noContent().build();
    }
}