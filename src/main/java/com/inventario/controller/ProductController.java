package com.inventario.controller;

import com.inventario.dto.ProductRequestDTO;
import com.inventario.dto.ProductResponseDTO;
import com.inventario.security.WarehouseSecurityService;
import com.inventario.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final WarehouseSecurityService warehouseSecurityService;

    // GET /api/v1/products -> Lista productos activos con existencias contextuales por almacén
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) Long warehouseId) {
        Long resolvedWarehouseId = warehouseSecurityService.resolveAccessibleWarehouseId(warehouseId);
        return ResponseEntity.ok(productService.getAllProducts(resolvedWarehouseId));
    }

    // GET /api/v1/products/{id} -> Busca un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // GET /api/v1/products/barcode/{barcode} -> Busca por código de barras (para el POS)
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponseDTO> getProductByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(productService.getProductByBarcode(barcode));
    }

    // GET /api/v1/products/low-stock -> Alerta de productos que necesitan reabastecimiento
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }

    // POST /api/v1/products -> Crea un nuevo producto (HTTP 201 Created)
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(requestDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // PUT /api/v1/products/{id} -> Actualiza un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, requestDTO));
    }

    // DELETE /api/v1/products/{id} -> Desactiva el producto (HTTP 204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
