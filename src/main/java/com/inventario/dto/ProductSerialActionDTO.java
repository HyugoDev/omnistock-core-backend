package com.inventario.dto;

import com.inventario.entity.enums.AssetStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSerialActionDTO {

    @NotNull(message = "El nuevo estado del activo es obligatorio")
    private AssetStatus newStatus;

    private String assignedTo; // Nombre del operador/empleado si es préstamo (LOANED)
    private String notes;      // Motivo de mantenimiento o devolución
}
