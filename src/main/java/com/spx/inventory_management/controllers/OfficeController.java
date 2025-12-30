package com.spx.inventory_management.controllers;

import com.spx.inventory_management.models.Office;
import com.spx.inventory_management.services.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offices")
public class OfficeController {

    @Autowired
    public OfficeService officeService;

    // CRUD METHOD FROM SERVICE LAYER

    // Retrieve all the offices
    @GetMapping("/all")
    public List<Office> getAllOffices() {
        return officeService.getAllOffices();
    }

    // Retrieve an office by its id
    @GetMapping("/{id}")
    public Office getOfficeById(@PathVariable long id) {
        return officeService.getOfficeById(id);
    }

    // Insert a new office into "offices" database table (when id == null in JSON)
    @PostMapping("/insert")
    public Office insertOffice(@RequestBody Office newOffice) {
        return officeService.newOffice(newOffice);
    }

    // Update an existing office (when id != null in JSON)
    @PutMapping("/update/{id}")
    public Office updateOffice(@PathVariable long id, @RequestBody Office updatedOffice) {
        return officeService.updateExistingOffice(id, updatedOffice);
    }

    // Delete an office by its id
    @DeleteMapping("/{id}")
    public void deleteOfficeById(@PathVariable long id) {
        officeService.deleteOfficeById(id);
    }

}
