package com.inventario.dto;

import com.inventario.entity.enums.ProductType;
import com.inventario.entity.enums.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "El código de barras es obligatorio")
    @Size(max = 50, message = "El código de barras no puede exceder 50 caracteres")
    private String barcode;

    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String sku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    private ProductType productType;

    private UnitOfMeasure unitOfMeasure;

    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    private BigDecimal purchasePrice;

    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor a cero")
    private BigDecimal salePrice;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser menor a cero")
    private Integer currentStock;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser menor a cero")
    private Integer minStock;

    private String attributesJson;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;
}
