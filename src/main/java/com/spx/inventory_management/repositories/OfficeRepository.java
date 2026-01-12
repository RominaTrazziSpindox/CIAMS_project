package com.spx.inventory_management.repositories;

import com.spx.inventory_management.models.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {

    Optional<Office> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    void deleteByNameIgnoreCase(String name);
}
