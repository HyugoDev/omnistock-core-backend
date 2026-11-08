package com.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSerialRequestDTO {

    @NotBlank(message = "El número de serie es obligatorio")
    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    private String serialNumber;

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotNull(message = "El almacén es obligatorio")
    private Long warehouseId;

    private String notes;
}
