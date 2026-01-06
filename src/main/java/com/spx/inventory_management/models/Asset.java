package com.spx.inventory_management.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity
@Table(name="assets")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Asset {

    // Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_asset", nullable = false)
    private long id;


    @Column(name="serial_number", nullable = false, unique = true)
    private String serialNumber;


    @Column(name="purchase_date")
    private LocalDate purchaseDate;


    // Relation with Asset table on database
    @ManyToOne // Owner side (many)
    // Foreign key. An asset must have an asset type.
    @JoinColumn(name = "id_asset_type", nullable = false)
    private AssetType assetType;

    // Relation with Office table on database
    @ManyToOne // Owner side (many)
    //Foreign key. An asset must have an office collocation.
    @JoinColumn(name = "id_office", nullable = false)
    private Office office;
}
