package com.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatchRequestDTO {

    @NotBlank(message = "El número o código de lote es obligatorio")
    @Size(max = 50, message = "El número de lote no puede exceder 50 caracteres")
    private String batchNumber;

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotNull(message = "El almacén es obligatorio")
    private Long warehouseId;

    private LocalDate manufacturingDate;

    @NotNull(message = "La fecha de caducidad es obligatoria")
    private LocalDate expirationDate;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private BigDecimal initialQuantity;

    private BigDecimal unitCost;
}
