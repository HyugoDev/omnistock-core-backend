package com.inventario.dto;

import com.inventario.entity.enums.MovementType;
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
public class StockMovementResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productBarcode;
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private String batchNumber;
    private String serialNumber;
    private MovementType movementType;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String referenceDocument;
    private String userId;
    private String userName;
    private String notes;
    private LocalDateTime createdAt;
}
