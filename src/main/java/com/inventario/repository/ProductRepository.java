package com.inventario.repository;

import com.inventario.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Búsqueda por código de barras para el lector del escáner
    Optional<Product> findByBarcode(String barcode);
    // Listar solo los productos activos (borrado lógico)
    List<Product> findByActiveTrue();
    // Consulta JPQL: Obtener productos con stock menor o igual al mínimo
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.currentStock <= p.minStock")
    List<Product> findLowStockProducts();
}
