package com.spx.inventory_management.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offices")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
@Getter
@Setter
@ToString

public class Office {

    // Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_office", nullable = false)
    private long id;


    @Column(nullable = false, length = 100, unique = true)
    private String name;


    // Relation with Asset table on database
    // It allows to have this method: office.getAssets()
    @OneToMany(mappedBy = "office") // Inverse side --> mappedBy
    @ToString.Exclude
    // HashSet: no duplicates, no fixed ordered of the items
    private Set<Asset> assets = new HashSet<>();

}



