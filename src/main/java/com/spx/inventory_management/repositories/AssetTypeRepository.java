package com.spx.inventory_management.repositories;

import com.spx.inventory_management.models.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {

    Optional<AssetType> findByAssetTypeNameIgnoreCase(String assetTypeName);

    boolean existsByAssetTypeNameIgnoreCase(String assetTypeName);

    void deleteByAssetTypeNameIgnoreCase(String assetTypeName);
}
