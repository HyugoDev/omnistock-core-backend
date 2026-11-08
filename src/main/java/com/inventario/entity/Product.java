package com.inventario.entity;

import com.inventario.entity.enums.ProductType;
import com.inventario.entity.enums.UnitOfMeasure;
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
        @UniqueConstraint(name = "uk_product_barcode", columnNames = "barcode"),
        @UniqueConstraint(name = "uk_product_sku", columnNames = "sku")
    },
    indexes = {
        @Index(name = "idx_product_barcode", columnList = "barcode", unique = true),
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_type", columnList = "product_type")
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

    @Column(name = "barcode", nullable = false, length = 50)
    private String barcode;

    @Column(name = "sku", length = 50)
    private String sku;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    // Naturaleza universal del artículo
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 30)
    private ProductType productType = ProductType.STANDARD;

    // Unidad de medida física
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false, length = 30)
    private UnitOfMeasure unitOfMeasure = UnitOfMeasure.UNIT;

    // Precios monetarios
    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice;

    // Saldo agregado global (la distribución granular se encuentra en WarehouseStock)
    @Builder.Default
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Builder.Default
    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 5;

    // Atributos dinámicos en formato JSON para adaptabilidad a cualquier industria
    // Ej: {"laboratorio": "Roche", "talla": "M", "voltaje": "110V"}
    @Column(name = "attributes_json", columnDefinition = "TEXT")
    private String attributesJson;

    // Borrado lógico (Soft Delete)
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // Categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
