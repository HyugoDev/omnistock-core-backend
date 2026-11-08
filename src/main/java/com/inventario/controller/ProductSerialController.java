package com.inventario.controller;

import com.inventario.dto.ProductSerialActionDTO;
import com.inventario.dto.ProductSerialRequestDTO;
import com.inventario.dto.ProductSerialResponseDTO;
import com.inventario.service.ProductSerialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/serials")
@RequiredArgsConstructor
public class ProductSerialController {

    private final ProductSerialService productSerialService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductSerialResponseDTO>> getSerialsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productSerialService.getSerialsByProduct(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<ProductSerialResponseDTO>> getSerialsByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(productSerialService.getSerialsByWarehouse(warehouseId));
    }

    @PostMapping
    public ResponseEntity<ProductSerialResponseDTO> registerSerial(@Valid @RequestBody ProductSerialRequestDTO requestDTO) {
        ProductSerialResponseDTO created = productSerialService.registerSerial(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ProductSerialResponseDTO> updateSerialStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductSerialActionDTO actionDTO) {
        return ResponseEntity.ok(productSerialService.updateSerialStatus(id, actionDTO));
    }
}
