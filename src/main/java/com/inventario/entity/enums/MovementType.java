package com.inventario.entity.enums;

public enum MovementType {
    PURCHASE_ENTRY,      // Entrada por orden de compra a proveedor
    SALE_POS,            // Salida por venta directa en mostrador POS
    TRANSFER_OUT,        // Salida desde bodega origen hacia otra sucursal
    TRANSFER_IN,         // Entrada confirmada en bodega destino
    MERMA_ADJUSTMENT,    // Salida por producto dañado, vencido o roto
    AUDIT_ADJUSTMENT,    // Ajuste contable tras toma física de inventario
    ASSET_LOAN,          // Préstamo de herramienta o activo a empleado
    ASSET_RETURN         // Devolución de herramienta o activo a bodega
}
