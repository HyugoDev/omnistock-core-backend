package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStockResponseDTO {

    private Long id;
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private Long productId;
    private String productBarcode;
    private String productSku;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal minStock;
    private BigDecimal maxStock;
}
