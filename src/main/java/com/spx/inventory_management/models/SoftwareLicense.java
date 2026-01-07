package com.spx.inventory_management.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    @Column(name="id_software_license", nullable = false)
    private long id;

    @Column(name="software_name", nullable = false, unique = true)
    private String softwareName;

    @Column(name = "max_installations")
    private Integer maxInstallations;

    @Column(name="expiration_date", nullable = false)
    private LocalDate expirationDate;


    // Relation with Asset table on database
    @ManyToMany
    @JoinTable(
            name = "assets_licenses", // Join table
            joinColumns = @JoinColumn(name = "license_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private Set<Asset> installedAssets;
}