package com.inventario.repository;

import com.inventario.entity.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {

    Optional<WarehouseStock> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    List<WarehouseStock> findByWarehouseId(Long warehouseId);

    List<WarehouseStock> findByProductId(Long productId);

    // Productos bajo el stock mínimo en un almacén específico
    @Query("SELECT ws FROM WarehouseStock ws WHERE ws.warehouse.id = :warehouseId AND ws.quantity <= ws.minStock")
    List<WarehouseStock> findLowStockByWarehouse(@Param("warehouseId") Long warehouseId);
}
