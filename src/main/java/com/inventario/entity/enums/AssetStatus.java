package com.inventario.entity.enums;

public enum AssetStatus {
    AVAILABLE,         // Disponible en bodega para uso o venta
    LOANED,            // Prestado a operador o departamento
    IN_MAINTENANCE,    // En taller o reparación técnica
    DECOMMISSIONED     // Dado de baja / scrap
}
