package com.spx.inventory_management.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "asset_types")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "assetTypeName")
@ToString
public class AssetType {

    // Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asset_type", nullable = false)
    private long id;


    @Column(nullable = false, length = 100, unique = true)
    private String assetTypeName;


    @Column(nullable = true, length = 200)
    private String assetTypeDescription;


    // Relation with Asset table on database
    // It allows to have this method: assetType.getAssets()
    @OneToMany(mappedBy = "assetType") // Inverse side --> mappedBy
    @ToString.Exclude
    // HashSet: no duplicates, no fixed ordered of the items
    private Set<Asset> assets = new HashSet<>();
}


