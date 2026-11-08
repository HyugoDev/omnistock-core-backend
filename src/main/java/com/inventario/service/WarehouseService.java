package com.inventario.service;

import com.inventario.dto.WarehouseRequestDTO;
import com.inventario.dto.WarehouseResponseDTO;
import com.inventario.dto.WarehouseStockResponseDTO;
import com.inventario.dto.WarehouseTransferRequestDTO;

import java.util.List;

public interface WarehouseService {

    List<WarehouseResponseDTO> getAllWarehouses();

    WarehouseResponseDTO getWarehouseById(Long id);

    WarehouseResponseDTO createWarehouse(WarehouseRequestDTO dto);

    List<WarehouseStockResponseDTO> getStockByWarehouse(Long warehouseId);

    void transferStock(WarehouseTransferRequestDTO dto);
}
