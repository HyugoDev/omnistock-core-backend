package com.inventario.dto;

import com.inventario.entity.enums.AssetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSerialResponseDTO {

    private Long id;
    private String serialNumber;
    private Long productId;
    private String productName;
    private String productBarcode;
    private Long warehouseId;
    private String warehouseName;
    private AssetStatus status;
    private String assignedTo;
    private LocalDateTime assignedAt;
    private String notes;
    private LocalDateTime createdAt;
}
