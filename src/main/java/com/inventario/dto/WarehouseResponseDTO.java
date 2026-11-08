package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String address;
    private Boolean active;
    private Boolean isTransit;
    private LocalDateTime createdAt;
}
