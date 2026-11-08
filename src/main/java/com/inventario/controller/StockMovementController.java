package com.inventario.controller;

import com.inventario.dto.StockMovementResponseDTO;
import com.inventario.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementResponseDTO>> getMovementsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByProduct(productId));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<StockMovementResponseDTO>> getLatestMovements(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(stockMovementService.getLatestMovements(limit));
    }
}
