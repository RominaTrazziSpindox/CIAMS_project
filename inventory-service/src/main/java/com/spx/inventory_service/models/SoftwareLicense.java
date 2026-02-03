package com.spx.inventory_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "software_licenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareLicense {

    // Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_software_license")
    private long id;

    @Column(name="software_name", nullable = false, unique = true, length = 200)
    private String softwareName;

    @Column(name = "max_installations")
    private Integer maxInstallations;

    @Column(name="expiration_date", nullable = false)
    private LocalDate expirationDate;

    // Methods
    public void addAsset(Asset asset) {
        this.installedAssets.add(asset);
        asset.getSoftwareLicenses().add(this);
    }

    public void removeAsset(Asset asset) {
        this.installedAssets.remove(asset);
        asset.getSoftwareLicenses().remove(this);
    }

    // Relation with Asset table on database
    @ManyToMany
    @JoinTable(
            name = "assets_licenses", // Join table
            joinColumns = @JoinColumn(name = "license_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private Set<Asset> installedAssets = new HashSet<>();
}