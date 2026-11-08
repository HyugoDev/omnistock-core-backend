package com.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseTransferRequestDTO {

    @NotNull(message = "El almacén de origen es obligatorio")
    private Long originWarehouseId;

    @NotNull(message = "El almacén de destino es obligatorio")
    private Long destinationWarehouseId;

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad a transferir es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private BigDecimal quantity;

    private String referenceDocument;
    private String notes;
}
