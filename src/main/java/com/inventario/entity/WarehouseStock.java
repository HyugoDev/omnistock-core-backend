package com.inventario.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "warehouse_stocks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_warehouse_product", columnNames = {"warehouse_id", "product_id"})
    },
    indexes = {
        @Index(name = "idx_stock_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_stock_product", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Cantidad física con soporte para decimales (Kg, Litros, Metros)
    @Builder.Default
    @Column(name = "quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "min_stock", nullable = false, precision = 12, scale = 4)
    private BigDecimal minStock = new BigDecimal("5.0000");

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isLowStock() {
        return this.quantity != null && this.minStock != null && this.quantity.compareTo(this.minStock) <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseStock that = (WarehouseStock) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
