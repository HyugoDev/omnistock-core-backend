package com.inventario.entity.enums;

public enum ProductType {
    STANDARD,          // Consumo masivo, retail básico (ej. refrescos, botanas, ropa)
    BATCH_EXPIRABLE,   // Perecederos, farmacéuticos, alimentos (control por lote y fecha de vencimiento FEFO)
    SERIALIZED_ASSET   // Activos fijos, maquinaria, equipos electrónicos (número de serie único por unidad física)
}
