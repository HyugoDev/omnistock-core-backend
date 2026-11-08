-- ==========================================================
-- OmniStock Enterprise WMS - Datos Semilla Universales (PostgreSQL 16)
-- ==========================================================

-- 1. Categorías Iniciales
INSERT INTO categories (id, name, description, active, created_at, updated_at)
VALUES 
    (1, 'Bebidas', 'Refrescos, jugos, aguas y bebidas energéticas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Snacks', 'Papas, galletas, botanas y confitería', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Lácteos', 'Leche, queso, yogur y derivados refrigerados', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'Farmacia & Salud', 'Medicamentos OTC, suplementos y primeros auxilios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'Herramientas & Equipos', 'Maquinaria ligera, herramientas y mantenimiento', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO UPDATE 
SET description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP;

SELECT setval(pg_get_serial_sequence('categories', 'id'), (SELECT COALESCE(MAX(id), 1) FROM categories));

-- 2. Almacenes y Sucursales (Multi-Bodega)
INSERT INTO warehouses (id, code, name, address, active, is_transit, created_at, updated_at)
VALUES
    (1, 'BOD-CENTRAL', 'Almacén Central Distribución', 'Av. Industrial 500, Parque Logístico', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'SUC-NORTE', 'Sucursal Norte Mostrador POS', 'Plaza Comercial Norte Local 12', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'SUC-SUR', 'Sucursal Sur Mostrador POS', 'Av. Insurgentes Sur 1420', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'TRANSIT-01', 'Unidad de Reparto 01 (En Tránsito)', 'Camión Freightliner Placas TR-902', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name,
    address = EXCLUDED.address,
    active = EXCLUDED.active,
    is_transit = EXCLUDED.is_transit,
    updated_at = CURRENT_TIMESTAMP;

SELECT setval(pg_get_serial_sequence('warehouses', 'id'), (SELECT COALESCE(MAX(id), 1) FROM warehouses));

-- 3. Catálogo Universal de Productos (Standard, Perecedero/Lote y Activo Serializado)
INSERT INTO products (id, barcode, sku, name, description, product_type, unit_of_measure, purchase_price, sale_price, current_stock, min_stock, attributes_json, active, category_id, created_at, updated_at)
VALUES 
    -- Retail Masivo / Standard
    (1, '7501001', 'SKU-BEB-01', 'Coca Cola 600ml', 'Refresco de cola en botella no retornable', 'STANDARD', 'UNIT', 12.00, 18.00, 48, 10, '{"presentacion": "Botella PET", "volumen": "600ml"}', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '7501002', 'SKU-SNK-01', 'Papas Fritas Sal 45g', 'Papas fritas con sal marina artesanal', 'STANDARD', 'UNIT', 10.00, 16.50, 4, 10, '{"gramaje": "45g", "tipo": "Sal Marina"}', true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, '7501003', 'SKU-BEB-02', 'Agua Mineral 1L', 'Agua purificada de manantial baja en sodio', 'STANDARD', 'UNIT', 8.50, 14.00, 36, 12, '{"minerales": "Bajo en sodio", "volumen": "1000ml"}', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, '7501004', 'SKU-LAC-01', 'Leche Entera 1L', 'Leche pasteurizada fortificada con vitaminas A y D', 'STANDARD', 'LITER', 18.00, 24.50, 24, 8, '{"grasa": "3.5%", "origen": "Vacuno"}', true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Farmacia / Lotes FEFO (BATCH_EXPIRABLE)
    (5, '7501006', 'SKU-FAR-01', 'Paracetamol 500mg 20 Tab', 'Caja de 20 tabletas analgésicas y antipiréticas', 'BATCH_EXPIRABLE', 'BOX', 15.00, 28.00, 60, 15, '{"laboratorio": "GenFar", "requiereReceta": false, "via": "Oral"}', true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, '7501007', 'SKU-FAR-02', 'Suero Oral Electrolitos 500ml', 'Solución rehidratante oral sabor manzana', 'BATCH_EXPIRABLE', 'UNIT', 13.50, 22.50, 25, 10, '{"sabor": "Manzana", "electrolitos": "Sodio, Potasio, Glucosa"}', true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Herramientas / Activos Serializados (SERIALIZED_ASSET)
    (7, '7501008', 'SKU-HER-01', 'Taladro Percutor 650W', 'Taladro profesional velocidad variable 1/2 pulgada', 'SERIALIZED_ASSET', 'UNIT', 420.00, 680.00, 3, 2, '{"marca": "Makita", "potencia": "650W", "mandril": "1/2 pulgada", "voltaje": "110V"}', true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, '7501009', 'SKU-HER-02', 'Juego de Destornilladores 6 Pzs', 'Kit magnético de cromo vanadio punta plana y cruz', 'STANDARD', 'BOX', 85.00, 145.00, 12, 4, '{"piezas": 6, "material": "Cromo Vanadio"}', true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (barcode) DO UPDATE 
SET name = EXCLUDED.name,
    sku = EXCLUDED.sku,
    description = EXCLUDED.description,
    product_type = EXCLUDED.product_type,
    unit_of_measure = EXCLUDED.unit_of_measure,
    purchase_price = EXCLUDED.purchase_price,
    sale_price = EXCLUDED.sale_price,
    current_stock = EXCLUDED.current_stock,
    min_stock = EXCLUDED.min_stock,
    attributes_json = EXCLUDED.attributes_json,
    active = EXCLUDED.active,
    category_id = EXCLUDED.category_id,
    updated_at = CURRENT_TIMESTAMP;

SELECT setval(pg_get_serial_sequence('products', 'id'), (SELECT COALESCE(MAX(id), 1) FROM products));

-- 4. Distribución de Stock por Almacén (WarehouseStock)
INSERT INTO warehouse_stocks (warehouse_id, product_id, quantity, min_stock, updated_at)
VALUES
    -- Almacén Central
    (1, 1, 36.0000, 5.0000, CURRENT_TIMESTAMP),  -- Coca Cola
    (1, 2, 0.0000, 5.0000, CURRENT_TIMESTAMP),   -- Papas agotadas en Central
    (1, 5, 40.0000, 10.0000, CURRENT_TIMESTAMP), -- Paracetamol
    (1, 7, 2.0000, 1.0000, CURRENT_TIMESTAMP),   -- Taladros en Central

    -- Sucursal Norte POS
    (2, 1, 12.0000, 5.0000, CURRENT_TIMESTAMP),  -- Coca Cola
    (2, 2, 4.0000, 5.0000, CURRENT_TIMESTAMP),   -- Papas (Stock bajo)
    (2, 5, 20.0000, 5.0000, CURRENT_TIMESTAMP),  -- Paracetamol
    (2, 7, 1.0000, 1.0000, CURRENT_TIMESTAMP)    -- 1 Taladro en exhibición
ON CONFLICT (warehouse_id, product_id) DO UPDATE
SET quantity = EXCLUDED.quantity,
    min_stock = EXCLUDED.min_stock,
    updated_at = CURRENT_TIMESTAMP;

-- 5. Lotes Farmacéuticos para Despacho FEFO (ProductBatch)
INSERT INTO product_batches (batch_number, product_id, warehouse_id, manufacturing_date, expiration_date, initial_quantity, current_quantity, unit_cost, active, created_at, updated_at)
VALUES
    -- Lote 1: Próximo a vencer (debe despacharse primero según FEFO)
    ('LOT-PARA-2026-A', 5, 1, '2024-01-15', '2026-12-31', 30.0000, 15.0000, 14.50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    -- Lote 2: Vencimiento más lejano
    ('LOT-PARA-2027-B', 5, 1, '2025-02-10', '2027-10-15', 25.0000, 25.0000, 15.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (batch_number, product_id, warehouse_id) DO UPDATE
SET current_quantity = EXCLUDED.current_quantity,
    expiration_date = EXCLUDED.expiration_date,
    updated_at = CURRENT_TIMESTAMP;

-- 6. Activos Serializados (ProductSerial)
INSERT INTO product_serials (serial_number, product_id, warehouse_id, status, assigned_to, assigned_at, notes, created_at, updated_at)
VALUES
    ('SN-MAKITA-88214', 7, 1, 'AVAILABLE', null, null, 'Equipo nuevo en estante A-3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SN-MAKITA-88215', 7, 1, 'LOANED', 'Carlos Gómez (Técnico Mant.)', CURRENT_TIMESTAMP, 'Prestado para mantenimiento en planta', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SN-MAKITA-88216', 7, 2, 'AVAILABLE', null, null, 'En vitrina Sucursal Norte', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (serial_number) DO UPDATE
SET status = EXCLUDED.status,
    assigned_to = EXCLUDED.assigned_to,
    notes = EXCLUDED.notes,
    updated_at = CURRENT_TIMESTAMP;
