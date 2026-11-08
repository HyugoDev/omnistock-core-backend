package com.inventario.service;

import com.inventario.dto.*;

import java.util.List;

public interface ProductBatchService {

    List<ProductBatchResponseDTO> getBatchesByProduct(Long productId);

    List<ProductBatchResponseDTO> getBatchesByWarehouse(Long warehouseId);

    ProductBatchResponseDTO createBatch(ProductBatchRequestDTO dto);

    FefoDispatchResponseDTO dispatchFefo(FefoDispatchRequestDTO dto);

    List<ProductBatchResponseDTO> getBatchesNearExpiration(int daysThreshold);
}
