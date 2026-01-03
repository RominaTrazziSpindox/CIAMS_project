package com.spx.inventory_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeRequestDTO {

    @NotBlank(message = "Office name cannot be blank")
    @Size(max = 100, message = "Office name must not exceed 100 characters")
    private String officeName;


}
