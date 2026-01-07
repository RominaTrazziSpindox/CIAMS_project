package com.spx.inventory_management.dto;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class SoftwareLicenseResponseDTO implements Serializable {

    private Long id;
    private String softwareName;
    private Integer maxInstallations;
    private LocalDate expirationDate;
}