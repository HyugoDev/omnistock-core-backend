package com.inventario.service.impl;

import com.inventario.dto.*;
import com.inventario.entity.*;
import com.inventario.entity.enums.MovementType;
import com.inventario.exception.BadRequestException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.repository.*;
import com.inventario.service.ProductBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductBatchServiceImpl implements ProductBatchService {

    private final ProductBatchRepository productBatchRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductBatchResponseDTO> getBatchesByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + productId);
        }
        return productBatchRepository.findBatchesForFefoDispatch(productId, null, BigDecimal.ZERO)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductBatchResponseDTO> getBatchesByWarehouse(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Almacén no encontrado con ID: " + warehouseId);
        }
        return productBatchRepository.findAll().stream()
                .filter(b -> b.getWarehouse().getId().equals(warehouseId))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductBatchResponseDTO createBatch(ProductBatchRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + dto.getWarehouseId()));

        if (dto.getManufacturingDate() != null && dto.getExpirationDate().isBefore(dto.getManufacturingDate())) {
            throw new BadRequestException("La fecha de vencimiento no puede ser anterior a la de fabricación");
        }

        if (productBatchRepository.findByBatchNumberAndProductIdAndWarehouseId(
                dto.getBatchNumber().trim(), product.getId(), warehouse.getId()).isPresent()) {
            throw new BadRequestException(String.format("El lote '%s' ya existe para este producto en el almacén '%s'",
                    dto.getBatchNumber(), warehouse.getName()));
        }

        // 1. Guardar nuevo lote
        ProductBatch batch = ProductBatch.builder()
                .batchNumber(dto.getBatchNumber().trim())
                .product(product)
                .warehouse(warehouse)
                .manufacturingDate(dto.getManufacturingDate())
                .expirationDate(dto.getExpirationDate())
                .initialQuantity(dto.getInitialQuantity())
                .currentQuantity(dto.getInitialQuantity())
                .unitCost(dto.getUnitCost())
                .active(true)
                .build();

        ProductBatch savedBatch = productBatchRepository.save(batch);

        // 2. Incrementar stock en WarehouseStock
        WarehouseStock stock = warehouseStockRepository
                .findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                .orElseGet(() -> WarehouseStock.builder()
                        .warehouse(warehouse)
                        .product(product)
                        .quantity(BigDecimal.ZERO)
                        .minStock(new BigDecimal("5.0000"))
                        .build());

        stock.setQuantity(stock.getQuantity().add(dto.getInitialQuantity()));
        warehouseStockRepository.save(stock);

        // 3. Incrementar stock general en Product
        product.setCurrentStock(product.getCurrentStock() + dto.getInitialQuantity().intValue());
        productRepository.save(product);

        // 4. Asiento en Kardex (StockMovement)
        StockMovement movement = StockMovement.builder()
                .movementType(MovementType.PURCHASE_ENTRY)
                .product(product)
                .destinationWarehouse(warehouse)
                .batch(savedBatch)
                .quantity(dto.getInitialQuantity())
                .unitCost(dto.getUnitCost())
                .totalCost(dto.getUnitCost() != null ? dto.getUnitCost().multiply(dto.getInitialQuantity()) : null)
                .notes("Ingreso inicial de lote: " + savedBatch.getBatchNumber())
                .build();
        stockMovementRepository.save(movement);

        log.info("Lote creado exitosamente: {} para el producto '{}'", savedBatch.getBatchNumber(), product.getName());
        return mapToResponse(savedBatch);
    }

    /**
     * ALGORITMO FEFO (First-Expired, First-Out):
     * Selecciona y consume los lotes ordenados cronológicamente por fecha de vencimiento ascendente.
     */
    @Override
    @Transactional
    public FefoDispatchResponseDTO dispatchFefo(FefoDispatchRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + dto.getWarehouseId()));

        // Obtener lotes disponibles ordenados por expirationDate ASC
        List<ProductBatch> availableBatches = productBatchRepository.findBatchesForFefoDispatch(
                product.getId(), warehouse.getId(), BigDecimal.ZERO);

        BigDecimal totalAvailable = availableBatches.stream()
                .map(ProductBatch::getCurrentQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(dto.getQuantity()) < 0) {
            throw new BadRequestException(String.format(
                    "Stock insuficiente en lotes para cumplir el despacho FEFO. Solicitado: %s, Disponible total en lotes: %s",
                    dto.getQuantity(), totalAvailable
            ));
        }

        BigDecimal remainingToDispatch = dto.getQuantity();
        List<FefoAllocationDTO> allocations = new ArrayList<>();

        for (ProductBatch b : availableBatches) {
            if (remainingToDispatch.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal availableInBatch = b.getCurrentQuantity();
            BigDecimal toTake = availableInBatch.min(remainingToDispatch);

            b.setCurrentQuantity(availableInBatch.subtract(toTake));
            productBatchRepository.save(b);

            allocations.add(FefoAllocationDTO.builder()
                    .batchId(b.getId())
                    .batchNumber(b.getBatchNumber())
                    .expirationDate(b.getExpirationDate())
                    .allocatedQuantity(toTake)
                    .remainingBatchQuantity(b.getCurrentQuantity())
                    .build());

            // Registrar movimiento en Kardex por cada lote consumido
            StockMovement mov = StockMovement.builder()
                    .movementType(MovementType.SALE_POS)
                    .product(product)
                    .sourceWarehouse(warehouse)
                    .batch(b)
                    .quantity(toTake)
                    .referenceDocument(dto.getReferenceDocument())
                    .notes("Despacho FEFO de lote " + b.getBatchNumber() + (dto.getNotes() != null ? " - " + dto.getNotes() : ""))
                    .build();
            stockMovementRepository.save(mov);

            remainingToDispatch = remainingToDispatch.subtract(toTake);
        }

        // Actualizar WarehouseStock
        warehouseStockRepository.findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                .ifPresent(ws -> {
                    ws.setQuantity(ws.getQuantity().subtract(dto.getQuantity()));
                    warehouseStockRepository.save(ws);
                });

        // Actualizar Product general stock
        product.setCurrentStock(Math.max(0, product.getCurrentStock() - dto.getQuantity().intValue()));
        productRepository.save(product);

        log.info("Despacho FEFO completado: {} unidades de '{}' despachadas desde {} lotes",
                dto.getQuantity(), product.getName(), allocations.size());

        return FefoDispatchResponseDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .warehouseId(warehouse.getId())
                .warehouseName(warehouse.getName())
                .requestedQuantity(dto.getQuantity())
                .dispatchedQuantity(dto.getQuantity())
                .fullyDispatched(true)
                .allocations(allocations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductBatchResponseDTO> getBatchesNearExpiration(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return productBatchRepository.findBatchesNearExpiration(thresholdDate).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProductBatchResponseDTO mapToResponse(ProductBatch b) {
        return ProductBatchResponseDTO.builder()
                .id(b.getId())
                .batchNumber(b.getBatchNumber())
                .productId(b.getProduct().getId())
                .productName(b.getProduct().getName())
                .productBarcode(b.getProduct().getBarcode())
                .warehouseId(b.getWarehouse().getId())
                .warehouseName(b.getWarehouse().getName())
                .manufacturingDate(b.getManufacturingDate())
                .expirationDate(b.getExpirationDate())
                .initialQuantity(b.getInitialQuantity())
                .currentQuantity(b.getCurrentQuantity())
                .unitCost(b.getUnitCost())
                .active(b.getActive())
                .isExpired(b.isExpired())
                .isNearExpiration(b.isNearExpiration())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
