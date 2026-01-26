package com.spx.inventory_management.repositories;

import com.spx.inventory_management.models.SoftwareLicense;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SoftwareLicenseRepository extends JpaRepository<SoftwareLicense, Long> {

    // ==========================================================
    // BASIC READ OPERATIONS
    // ==========================================================

    Optional<SoftwareLicense> findBySoftwareNameIgnoreCase(String softwareName);

    boolean existsBySoftwareNameIgnoreCase(String softwareName);

    void deleteBySoftwareNameIgnoreCase(String softwareName);

    // ==========================================================
    // COMPLIANCE & AUDIT QUERIES
    // ==========================================================

    // Licenses installed on a specific asset (by serial number)
    List<SoftwareLicense> findByInstalledAssets_SerialNumberIgnoreCase(
            String serialNumber
    );

    // Licenses expiring before a given date
    @EntityGraph(attributePaths = "installedAssets")
    List<SoftwareLicense> findByExpirationDateBetween(LocalDate start, LocalDate end);
}