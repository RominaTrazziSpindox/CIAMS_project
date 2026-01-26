package com.spx.inventory_management.repositories;

import com.spx.inventory_management.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository <Asset, Long> {

    boolean existsBySerialNumberIgnoreCase(String serialNumber);

    Optional<Asset> findBySerialNumberIgnoreCase(String serialNumber);

    void deleteBySerialNumberIgnoreCase(String serialNumber);

    List<Asset> findByOffice_NameIgnoreCase(String officeName);

    List<Asset> findByAssetType_AssetTypeNameIgnoreCase(String assetTypeName);
}

