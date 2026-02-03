package com.spx.inventory_service.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OfficeResponseDTO implements Serializable {

    private Long id;
    private String officeName;
}

