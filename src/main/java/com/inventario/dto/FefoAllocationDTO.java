package com.inventario.dto;

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
public class FefoAllocationDTO {

    private Long batchId;
    private String batchNumber;
    private LocalDate expirationDate;
    private BigDecimal allocatedQuantity;
    private BigDecimal remainingBatchQuantity;
}
