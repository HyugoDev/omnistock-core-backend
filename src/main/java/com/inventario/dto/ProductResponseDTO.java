package com.inventario.dto;

import com.inventario.entity.enums.ProductType;
import com.inventario.entity.enums.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String barcode;
    private String sku;
    private String name;
    private String description;
    private ProductType productType;
    private UnitOfMeasure unitOfMeasure;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Integer currentStock;
    private Integer minStock;
    private String attributesJson;
    private Boolean active;
    private Boolean requiresRestock;

    // Datos aplanados de la categoría (evita problemas de Lazy loading)
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
