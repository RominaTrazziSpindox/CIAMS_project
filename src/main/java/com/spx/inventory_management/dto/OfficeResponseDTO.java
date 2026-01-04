package com.spx.inventory_management.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class OfficeResponseDTO implements Serializable {

    private Long id;
    private String officeName;
}

