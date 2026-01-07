package com.spx.inventory_management.repositories;

import com.spx.inventory_management.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository <Asset, Long> {

    boolean existsBySerialNumber(String serialNumber);

    Optional<Asset> findBySerialNumber(String serialNumber);

    void deleteBySerialNumber(String serialNumber);

}
