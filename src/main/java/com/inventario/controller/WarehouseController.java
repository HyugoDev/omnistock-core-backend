package com.inventario.controller;

import com.inventario.dto.WarehouseRequestDTO;
import com.inventario.dto.WarehouseResponseDTO;
import com.inventario.dto.WarehouseStockResponseDTO;
import com.inventario.dto.WarehouseTransferRequestDTO;
import com.inventario.security.WarehouseSecurityService;
import com.inventario.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseSecurityService warehouseSecurityService;

    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getWarehouseById(@PathVariable Long id) {
        warehouseSecurityService.validateCanOperateOnWarehouse(id);
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(@Valid @RequestBody WarehouseRequestDTO requestDTO) {
        WarehouseResponseDTO created = warehouseService.createWarehouse(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<List<WarehouseStockResponseDTO>> getStockByWarehouse(@PathVariable Long id) {
        warehouseSecurityService.validateCanOperateOnWarehouse(id);
        return ResponseEntity.ok(warehouseService.getStockByWarehouse(id));
    }

    @PostMapping("/transfers")
    public ResponseEntity<Map<String, String>> transferStock(@Valid @RequestBody WarehouseTransferRequestDTO requestDTO) {
        warehouseSecurityService.validateCanOperateOnWarehouse(requestDTO.getOriginWarehouseId());
        warehouseService.transferStock(requestDTO);
        return ResponseEntity.ok(Map.of("message", "Transferencia de existencias procesada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequestDTO requestDTO) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
