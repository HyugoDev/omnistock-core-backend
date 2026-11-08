package com.inventario.repository;

import com.inventario.entity.ProductSerial;
import com.inventario.entity.enums.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {

    Optional<ProductSerial> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    List<ProductSerial> findByProductId(Long productId);

    List<ProductSerial> findByProductIdAndStatus(Long productId, AssetStatus status);

    List<ProductSerial> findByWarehouseIdAndStatus(Long warehouseId, AssetStatus status);

    // Herramientas o maquinaria actualmente prestadas a empleados
    List<ProductSerial> findByAssignedToIsNotNull();
}
