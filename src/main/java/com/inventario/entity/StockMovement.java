package com.inventario.entity;

import com.inventario.entity.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "stock_movements",
    indexes = {
        @Index(name = "idx_mov_product", columnList = "product_id"),
        @Index(name = "idx_mov_created_at", columnList = "created_at"),
        @Index(name = "idx_mov_type", columnList = "movement_type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 35)
    private MovementType movementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Almacén origen (en caso de salida o transferencia)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id")
    private Warehouse sourceWarehouse;

    // Almacén destino (en caso de entrada o transferencia)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;

    // Lote vinculado (si el producto es perecedero)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private ProductBatch batch;

    // Número de serie vinculado (si es un activo específico)
    @Column(name = "serial_number", length = 80)
    private String serialNumber;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_cost", precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost;

    // Documento de soporte (No. Factura, Folio de Venta POS, Guía de Remisión)
    @Column(name = "reference_document", length = 100)
    private String referenceDocument;

    // Auditoría: Identidad del operador en Better Auth
    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "user_name", length = 150)
    private String userName;

    @Column(name = "notes", length = 500)
    private String notes;

    // Registro inmutable: fecha y hora exacta del asiento contable
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
