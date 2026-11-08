package com.inventario.service;

import com.inventario.dto.ProductRequestDTO;
import com.inventario.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO getProductByBarcode(String barcode);
    List<ProductResponseDTO> getLowStockProducts();
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);
    void deleteProduct(Long id);
}
