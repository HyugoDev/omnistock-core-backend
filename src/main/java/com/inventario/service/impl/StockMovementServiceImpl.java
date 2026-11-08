package com.inventario.service.impl;

import com.inventario.dto.StockMovementResponseDTO;
import com.inventario.entity.StockMovement;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.repository.ProductRepository;
import com.inventario.repository.StockMovementRepository;
import com.inventario.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> getMovementsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + productId);
        }
        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> getLatestMovements(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return stockMovementRepository.findAll(PageRequest.of(0, safeLimit)).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private StockMovementResponseDTO mapToResponse(StockMovement m) {
        return StockMovementResponseDTO.builder()
                .id(m.getId())
                .productId(m.getProduct().getId())
                .productName(m.getProduct().getName())
                .productBarcode(m.getProduct().getBarcode())
                .sourceWarehouseId(m.getSourceWarehouse() != null ? m.getSourceWarehouse().getId() : null)
                .sourceWarehouseName(m.getSourceWarehouse() != null ? m.getSourceWarehouse().getName() : null)
                .destinationWarehouseId(m.getDestinationWarehouse() != null ? m.getDestinationWarehouse().getId() : null)
                .destinationWarehouseName(m.getDestinationWarehouse() != null ? m.getDestinationWarehouse().getName() : null)
                .batchNumber(m.getBatch() != null ? m.getBatch().getBatchNumber() : null)
                .serialNumber(m.getSerialNumber())
                .movementType(m.getMovementType())
                .quantity(m.getQuantity())
                .unitCost(m.getUnitCost())
                .totalCost(m.getTotalCost())
                .referenceDocument(m.getReferenceDocument())
                .userId(m.getUserId())
                .userName(m.getUserName())
                .notes(m.getNotes())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
