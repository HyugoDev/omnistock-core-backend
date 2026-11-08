package com.inventario.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "product_batches",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_batch_product_warehouse", columnNames = {"batch_number", "product_id", "warehouse_id"})
    },
    indexes = {
        @Index(name = "idx_batch_expiration", columnList = "expiration_date"),
        @Index(name = "idx_batch_product", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_number", nullable = false, length = 60)
    private String batchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;

    // Fecha crítica para despacho inteligente FEFO (First Expired, First Out)
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "initial_quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal initialQuantity;

    @Column(name = "current_quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal currentQuantity;

    @Column(name = "unit_cost", precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isExpired() {
        return this.expirationDate != null && this.expirationDate.isBefore(LocalDate.now());
    }

    public boolean isNearExpiration() {
        return isNearExpiration(30L);
    }

    public boolean isNearExpiration(long daysThreshold) {
        if (this.expirationDate == null) return false;
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return !isExpired() && (this.expirationDate.isBefore(thresholdDate) || this.expirationDate.isEqual(thresholdDate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductBatch that = (ProductBatch) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
