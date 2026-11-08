package com.inventario.service.impl;

import com.inventario.dto.WarehouseRequestDTO;
import com.inventario.dto.WarehouseResponseDTO;
import com.inventario.dto.WarehouseStockResponseDTO;
import com.inventario.dto.WarehouseTransferRequestDTO;
import com.inventario.entity.Product;
import com.inventario.entity.StockMovement;
import com.inventario.entity.Warehouse;
import com.inventario.entity.WarehouseStock;
import com.inventario.entity.enums.MovementType;
import com.inventario.exception.BadRequestException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.repository.ProductRepository;
import com.inventario.repository.StockMovementRepository;
import com.inventario.repository.WarehouseRepository;
import com.inventario.repository.WarehouseStockRepository;
import com.inventario.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + id));
        return mapToResponse(warehouse);
    }

    @Override
    @Transactional
    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO dto) {
        if (warehouseRepository.findByCode(dto.getCode().trim().toUpperCase()).isPresent()) {
            throw new BadRequestException("Ya existe un almacén con el código: " + dto.getCode());
        }

        Warehouse warehouse = Warehouse.builder()
                .code(dto.getCode().trim().toUpperCase())
                .name(dto.getName().trim())
                .address(dto.getAddress() != null ? dto.getAddress().trim() : null)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .isTransit(dto.getIsTransit() != null ? dto.getIsTransit() : false)
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Almacén creado exitosamente: {} ({})", saved.getName(), saved.getCode());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseStockResponseDTO> getStockByWarehouse(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Almacén no encontrado con ID: " + warehouseId);
        }

        return warehouseStockRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapStockToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void transferStock(WarehouseTransferRequestDTO dto) {
        if (dto.getOriginWarehouseId().equals(dto.getDestinationWarehouseId())) {
            throw new BadRequestException("El almacén de origen y destino no pueden ser el mismo.");
        }

        Warehouse origin = warehouseRepository.findById(dto.getOriginWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Almacén de origen no encontrado con ID: " + dto.getOriginWarehouseId()));

        Warehouse destination = warehouseRepository.findById(dto.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Almacén de destino no encontrado con ID: " + dto.getDestinationWarehouseId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductId()));

        // 1. Obtener y validar stock en almacén de origen
        WarehouseStock originStock = warehouseStockRepository
                .findByWarehouseIdAndProductId(origin.getId(), product.getId())
                .orElseThrow(() -> new BadRequestException("El producto no cuenta con registro de stock en el almacén de origen (" + origin.getName() + ")"));

        if (originStock.getQuantity().compareTo(dto.getQuantity()) < 0) {
            throw new BadRequestException(String.format(
                    "Stock insuficiente en origen (%s). Disponible: %s, Solicitado: %s",
                    origin.getName(), originStock.getQuantity(), dto.getQuantity()
            ));
        }

        // 2. Decrementar stock en origen
        originStock.setQuantity(originStock.getQuantity().subtract(dto.getQuantity()));
        warehouseStockRepository.save(originStock);

        // 3. Registrar movimiento de salida por transferencia
        StockMovement movOut = StockMovement.builder()
                .movementType(MovementType.TRANSFER_OUT)
                .product(product)
                .sourceWarehouse(origin)
                .destinationWarehouse(destination)
                .quantity(dto.getQuantity())
                .referenceDocument(dto.getReferenceDocument())
                .notes(dto.getNotes() != null ? dto.getNotes() : "Transferencia hacia " + destination.getName())
                .build();
        stockMovementRepository.save(movOut);

        // 4. Incrementar stock en destino (crear registro si no existía)
        WarehouseStock destStock = warehouseStockRepository
                .findByWarehouseIdAndProductId(destination.getId(), product.getId())
                .orElseGet(() -> WarehouseStock.builder()
                        .warehouse(destination)
                        .product(product)
                        .quantity(BigDecimal.ZERO)
                        .minStock(new BigDecimal("5.0000"))
                        .build());

        destStock.setQuantity(destStock.getQuantity().add(dto.getQuantity()));
        warehouseStockRepository.save(destStock);

        // 5. Registrar movimiento de entrada por transferencia
        StockMovement movIn = StockMovement.builder()
                .movementType(MovementType.TRANSFER_IN)
                .product(product)
                .sourceWarehouse(origin)
                .destinationWarehouse(destination)
                .quantity(dto.getQuantity())
                .referenceDocument(dto.getReferenceDocument())
                .notes(dto.getNotes() != null ? dto.getNotes() : "Recepción de transferencia desde " + origin.getName())
                .build();
        stockMovementRepository.save(movIn);

        log.info("Transferencia de stock exitosa: {} unidades de '{}' desde '{}' hacia '{}'",
                dto.getQuantity(), product.getName(), origin.getName(), destination.getName());
    }

    @Override
    @Transactional
    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + id));

        warehouse.setName(dto.getName().trim());
        if (dto.getAddress() != null) {
            warehouse.setAddress(dto.getAddress().trim());
        }
        if (dto.getActive() != null) {
            warehouse.setActive(dto.getActive());
        }
        if (dto.getIsTransit() != null) {
            warehouse.setIsTransit(dto.getIsTransit());
        }

        Warehouse updated = warehouseRepository.save(warehouse);
        log.info("Almacén actualizado exitosamente: {} ({})", updated.getName(), updated.getCode());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + id));

        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
        log.info("Almacén desactivado exitosamente: {} ({})", warehouse.getName(), warehouse.getCode());
    }

    private WarehouseResponseDTO mapToResponse(Warehouse warehouse) {
        return WarehouseResponseDTO.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .name(warehouse.getName())
                .address(warehouse.getAddress())
                .active(warehouse.getActive())
                .isTransit(warehouse.getIsTransit())
                .createdAt(warehouse.getCreatedAt())
                .build();
    }

    private WarehouseStockResponseDTO mapStockToResponse(WarehouseStock stock) {
        return WarehouseStockResponseDTO.builder()
                .id(stock.getId())
                .warehouseId(stock.getWarehouse().getId())
                .warehouseCode(stock.getWarehouse().getCode())
                .warehouseName(stock.getWarehouse().getName())
                .productId(stock.getProduct().getId())
                .productBarcode(stock.getProduct().getBarcode())
                .productSku(stock.getProduct().getSku())
                .productName(stock.getProduct().getName())
                .quantity(stock.getQuantity())
                .minStock(stock.getMinStock())
                .build();
    }
}
