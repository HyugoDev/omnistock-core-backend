package com.inventario.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_barcode", columnNames = "barcode")
    },
    indexes = {
        @Index(name = "idx_product_barcode", columnList = "barcode", unique = true),
        @Index(name = "idx_product_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "barcode", nullable = false, unique = true, length = 50)
    private String barcode;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    // Regla Senior: DINERO SIEMPRE CON BigDecimal, NUNCA double o float (evita errores de redondeo)
    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Builder.Default
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock  = 0;

    @Builder.Default
    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 5;

    // Borrado lógico (Soft delete): los productos con ventas nunca se borran físicamente
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // Regla Senior: Relaciones @ManyToOne SIEMPRE en FetchType.LAZY para evitar el problema N+1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Método de dominio: Regla de negocio para saber si requiere reabastecimiento
    public boolean requiresRestock() {
        return this.currentStock != null && this.currentStock <= this.minStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
