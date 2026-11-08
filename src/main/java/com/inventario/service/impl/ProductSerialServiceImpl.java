package com.inventario.service.impl;

import com.inventario.dto.ProductSerialActionDTO;
import com.inventario.dto.ProductSerialRequestDTO;
import com.inventario.dto.ProductSerialResponseDTO;
import com.inventario.entity.Product;
import com.inventario.entity.ProductSerial;
import com.inventario.entity.StockMovement;
import com.inventario.entity.Warehouse;
import com.inventario.entity.enums.AssetStatus;
import com.inventario.entity.enums.MovementType;
import com.inventario.exception.BadRequestException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.repository.ProductRepository;
import com.inventario.repository.ProductSerialRepository;
import com.inventario.repository.StockMovementRepository;
import com.inventario.repository.WarehouseRepository;
import com.inventario.service.ProductSerialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSerialServiceImpl implements ProductSerialService {

    private final ProductSerialRepository productSerialRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductSerialResponseDTO> getSerialsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + productId);
        }
        return productSerialRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSerialResponseDTO> getSerialsByWarehouse(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Almacén no encontrado con ID: " + warehouseId);
        }
        return productSerialRepository.findAll().stream()
                .filter(s -> s.getWarehouse().getId().equals(warehouseId))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductSerialResponseDTO registerSerial(ProductSerialRequestDTO dto) {
        if (productSerialRepository.existsBySerialNumber(dto.getSerialNumber().trim())) {
            throw new BadRequestException("Ya existe un activo registrado con el número de serie: " + dto.getSerialNumber());
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + dto.getWarehouseId()));

        ProductSerial serial = ProductSerial.builder()
                .serialNumber(dto.getSerialNumber().trim())
                .product(product)
                .warehouse(warehouse)
                .status(AssetStatus.AVAILABLE)
                .notes(dto.getNotes() != null ? dto.getNotes().trim() : null)
                .build();

        ProductSerial saved = productSerialRepository.save(serial);
        log.info("Activo serializado registrado exitosamente: {} para el producto '{}'",
                saved.getSerialNumber(), product.getName());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProductSerialResponseDTO updateSerialStatus(Long serialId, ProductSerialActionDTO dto) {
        ProductSerial serial = productSerialRepository.findById(serialId)
                .orElseThrow(() -> new ResourceNotFoundException("Activo serializado no encontrado con ID: " + serialId));

        AssetStatus previousStatus = serial.getStatus();
        AssetStatus newStatus = dto.getNewStatus();

        if (newStatus == AssetStatus.LOANED) {
            if (dto.getAssignedTo() == null || dto.getAssignedTo().isBlank()) {
                throw new BadRequestException("Debe especificar el nombre del responsable para el préstamo del activo");
            }
            serial.setStatus(AssetStatus.LOANED);
            serial.setAssignedTo(dto.getAssignedTo().trim());
            serial.setAssignedAt(LocalDateTime.now());

            // Registrar movimiento de préstamo
            StockMovement loanMov = StockMovement.builder()
                    .movementType(MovementType.ASSET_LOAN)
                    .product(serial.getProduct())
                    .sourceWarehouse(serial.getWarehouse())
                    .serialNumber(serial.getSerialNumber())
                    .quantity(BigDecimal.ONE)
                    .userName(dto.getAssignedTo().trim())
                    .notes("Préstamo asignado a: " + dto.getAssignedTo().trim() + (dto.getNotes() != null ? " - " + dto.getNotes() : ""))
                    .build();
            stockMovementRepository.save(loanMov);

        } else if (newStatus == AssetStatus.AVAILABLE) {
            String returnedFrom = serial.getAssignedTo();
            serial.setStatus(AssetStatus.AVAILABLE);
            serial.setAssignedTo(null);
            serial.setAssignedAt(null);

            if (previousStatus == AssetStatus.LOANED) {
                StockMovement returnMov = StockMovement.builder()
                        .movementType(MovementType.ASSET_RETURN)
                        .product(serial.getProduct())
                        .destinationWarehouse(serial.getWarehouse())
                        .serialNumber(serial.getSerialNumber())
                        .quantity(BigDecimal.ONE)
                        .userName(returnedFrom)
                        .notes("Devolución a bodega desde: " + returnedFrom + (dto.getNotes() != null ? " - " + dto.getNotes() : ""))
                        .build();
                stockMovementRepository.save(returnMov);
            }
        } else {
            serial.setStatus(newStatus);
        }

        if (dto.getNotes() != null && !dto.getNotes().isBlank()) {
            serial.setNotes(dto.getNotes().trim());
        }

        ProductSerial updated = productSerialRepository.save(serial);
        log.info("Estado de activo '{}' actualizado de {} a {}",
                updated.getSerialNumber(), previousStatus, newStatus);

        return mapToResponse(updated);
    }

    private ProductSerialResponseDTO mapToResponse(ProductSerial s) {
        return ProductSerialResponseDTO.builder()
                .id(s.getId())
                .serialNumber(s.getSerialNumber())
                .productId(s.getProduct().getId())
                .productName(s.getProduct().getName())
                .productBarcode(s.getProduct().getBarcode())
                .warehouseId(s.getWarehouse().getId())
                .warehouseName(s.getWarehouse().getName())
                .status(s.getStatus())
                .assignedTo(s.getAssignedTo())
                .assignedAt(s.getAssignedAt())
                .notes(s.getNotes())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
