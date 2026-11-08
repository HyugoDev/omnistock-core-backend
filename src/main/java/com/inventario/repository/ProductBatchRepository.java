package com.inventario.repository;

import com.inventario.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    Optional<ProductBatch> findByBatchNumberAndProductIdAndWarehouseId(String batchNumber, Long productId, Long warehouseId);

    // Consulta FEFO Inteligente: Lotes disponibles ordenados por fecha de expiración más próxima
    @Query("""
        SELECT pb FROM ProductBatch pb 
        WHERE pb.product.id = :productId 
          AND pb.warehouse.id = :warehouseId 
          AND pb.currentQuantity > :minQty 
          AND pb.active = true 
        ORDER BY pb.expirationDate ASC
    """)
    List<ProductBatch> findBatchesForFefoDispatch(
        @Param("productId") Long productId,
        @Param("warehouseId") Long warehouseId,
        @Param("minQty") BigDecimal minQty
    );

    // Lotes próximos a vencer antes de una fecha límite
    @Query("""
        SELECT pb FROM ProductBatch pb 
        WHERE pb.expirationDate <= :thresholdDate 
          AND pb.currentQuantity > 0 
          AND pb.active = true 
        ORDER BY pb.expirationDate ASC
    """)
    List<ProductBatch> findBatchesNearExpiration(@Param("thresholdDate") LocalDate thresholdDate);
}
