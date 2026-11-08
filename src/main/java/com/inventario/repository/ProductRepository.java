package com.inventario.repository;

import com.inventario.entity.Product;
import com.inventario.entity.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Búsqueda por código de barras para el lector del escáner
    Optional<Product> findByBarcode(String barcode);

    Optional<Product> findBySku(String sku);

    // Listar solo los productos activos (borrado lógico)
    List<Product> findByActiveTrue();

    // Filtrar por naturaleza del producto (STANDARD, BATCH_EXPIRABLE, SERIALIZED_ASSET)
    List<Product> findByProductTypeAndActiveTrue(ProductType productType);

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    boolean existsByBarcode(String barcode);

    boolean existsBySku(String sku);

    // Consulta JPQL: Obtener productos con stock menor o igual al mínimo
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.currentStock <= p.minStock")
    List<Product> findLowStockProducts();
}
