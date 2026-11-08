package com.inventario.entity;

import com.inventario.entity.enums.AssetStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "product_serials",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_serial_number", columnNames = "serial_number")
    },
    indexes = {
        @Index(name = "idx_serial_number", columnList = "serial_number", unique = true),
        @Index(name = "idx_serial_status", columnList = "status"),
        @Index(name = "idx_serial_product", columnList = "product_id"),
        @Index(name = "idx_serial_warehouse", columnList = "warehouse_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSerial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número de serie, chasis, IMEI o identificador único del activo
    @Column(name = "serial_number", nullable = false, length = 80)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Almacén o taller donde está resguardado el activo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AssetStatus status = AssetStatus.AVAILABLE;

    // Empleado u operador al que se le prestó la herramienta/activo
    @Column(name = "assigned_to", length = 150)
    private String assignedTo;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSerial that = (ProductSerial) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
