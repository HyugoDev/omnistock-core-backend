package com.inventario.service;

import com.inventario.dto.StockMovementResponseDTO;

import java.util.List;

public interface StockMovementService {

    List<StockMovementResponseDTO> getMovementsByProduct(Long productId);

    List<StockMovementResponseDTO> getLatestMovements(int limit);
}
