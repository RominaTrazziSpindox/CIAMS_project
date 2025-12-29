package com.spx.inventory_management.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Office {

    // Properties

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_office", nullable = false)
    private Long id;

    @Column(name="name", nullable = false, length = 100, unique = true)
    private String name;

}
