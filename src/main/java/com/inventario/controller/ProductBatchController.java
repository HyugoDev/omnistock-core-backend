package com.inventario.controller;

import com.inventario.dto.*;
import com.inventario.service.ProductBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
public class ProductBatchController {

    private final ProductBatchService productBatchService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductBatchResponseDTO>> getBatchesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productBatchService.getBatchesByProduct(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<ProductBatchResponseDTO>> getBatchesByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(productBatchService.getBatchesByWarehouse(warehouseId));
    }

    @PostMapping
    public ResponseEntity<ProductBatchResponseDTO> createBatch(@Valid @RequestBody ProductBatchRequestDTO requestDTO) {
        ProductBatchResponseDTO created = productBatchService.createBatch(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/fefo-dispatch")
    public ResponseEntity<FefoDispatchResponseDTO> dispatchFefo(@Valid @RequestBody FefoDispatchRequestDTO requestDTO) {
        return ResponseEntity.ok(productBatchService.dispatchFefo(requestDTO));
    }

    @GetMapping("/near-expiration")
    public ResponseEntity<List<ProductBatchResponseDTO>> getBatchesNearExpiration(
            @RequestParam(defaultValue = "30") int daysThreshold) {
        return ResponseEntity.ok(productBatchService.getBatchesNearExpiration(daysThreshold));
    }
}
