package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FefoDispatchResponseDTO {

    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal requestedQuantity;
    private BigDecimal dispatchedQuantity;
    private Boolean fullyDispatched;
    private List<FefoAllocationDTO> allocations;
}
