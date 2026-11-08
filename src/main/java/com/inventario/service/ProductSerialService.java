package com.inventario.service;

import com.inventario.dto.ProductSerialActionDTO;
import com.inventario.dto.ProductSerialRequestDTO;
import com.inventario.dto.ProductSerialResponseDTO;

import java.util.List;

public interface ProductSerialService {

    List<ProductSerialResponseDTO> getSerialsByProduct(Long productId);

    List<ProductSerialResponseDTO> getSerialsByWarehouse(Long warehouseId);

    ProductSerialResponseDTO registerSerial(ProductSerialRequestDTO dto);

    ProductSerialResponseDTO updateSerialStatus(Long serialId, ProductSerialActionDTO dto);
}
