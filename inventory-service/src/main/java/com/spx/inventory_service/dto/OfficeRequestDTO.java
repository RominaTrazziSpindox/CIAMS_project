package com.spx.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class OfficeRequestDTO implements Serializable {

    @NotBlank(message = "Office name cannot be blank")
    @Size(max = 100, message = "Office name must not exceed 100 characters")
    private String officeName;

}


