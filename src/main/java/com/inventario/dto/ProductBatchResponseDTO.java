package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatchResponseDTO {

    private Long id;
    private String batchNumber;
    private Long productId;
    private String productName;
    private String productBarcode;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate manufacturingDate;
    private LocalDate expirationDate;
    private BigDecimal initialQuantity;
    private BigDecimal currentQuantity;
    private BigDecimal unitCost;
    private Boolean active;
    private Boolean isExpired;
    private Boolean isNearExpiration;
    private LocalDateTime createdAt;
}
