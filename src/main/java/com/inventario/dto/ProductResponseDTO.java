package com.inventario.dto;


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
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Integer currentStock;
    private Integer minStock;
    private Boolean active;
    private Boolean requiresRestock;
    // Datos aplanados de la categoría (evita problemas de Lazy loading)
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
