package com.spx.inventory_management.repositories;

import com.spx.inventory_management.models.SoftwareLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SoftwareLicenseRepository extends JpaRepository<SoftwareLicense, Long> {

    boolean existsBySoftwareName(String softwareName);

    List<SoftwareLicense> findByExpirationDateBetween(LocalDate start, LocalDate end);
}
