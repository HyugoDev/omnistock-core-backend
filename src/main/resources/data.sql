-- ==========================================================
-- OmniStock Enterprise WMS - Datos Semilla para PostgreSQL
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

-- Sincronizar secuencia de IDs de categorías
SELECT setval(pg_get_serial_sequence('categories', 'id'), (SELECT COALESCE(MAX(id), 1) FROM categories));

-- 2. Productos Iniciales (Consumibles, Lotes y Activos)
INSERT INTO products (barcode, name, description, purchase_price, sale_price, current_stock, min_stock, active, category_id, created_at, updated_at)
VALUES 
    ('7501001', 'Coca Cola 600ml', 'Refresco de cola en botella no retornable', 12.00, 18.00, 48, 10, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501002', 'Papas Fritas Sal 45g', 'Papas fritas con sal marina artesanal', 10.00, 16.50, 4, 10, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501003', 'Agua Mineral 1L', 'Agua purificada de manantial baja en sodio', 8.50, 14.00, 36, 12, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501004', 'Leche Entera 1L', 'Leche pasteurizada fortificada con vitaminas A y D', 18.00, 24.50, 24, 8, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501005', 'Yogur Griego Natural 150g', 'Yogur sin azúcar alto en proteínas', 14.00, 22.00, 15, 5, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501006', 'Paracetamol 500mg 20 Tab', 'Caja de 20 tabletas analgésicas y antipiréticas', 15.00, 28.00, 60, 15, true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501007', 'Suero Oral Electrolitos 500ml', 'Solución rehidratante oral sabor manzana', 13.50, 22.50, 3, 10, true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501008', 'Taladro Percutor 650W', 'Taladro profesional velocidad variable 1/2 pulgada', 420.00, 680.00, 6, 2, true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7501009', 'Juego de Destornilladores 6 Pzs', 'Kit magnético de cromo vanadio punta plana y cruz', 85.00, 145.00, 12, 4, true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (barcode) DO UPDATE 
SET name = EXCLUDED.name,
    description = EXCLUDED.description,
    purchase_price = EXCLUDED.purchase_price,
    sale_price = EXCLUDED.sale_price,
    current_stock = EXCLUDED.current_stock,
    min_stock = EXCLUDED.min_stock,
    active = EXCLUDED.active,
    category_id = EXCLUDED.category_id,
    updated_at = CURRENT_TIMESTAMP;

-- Sincronizar secuencia de IDs de productos
SELECT setval(pg_get_serial_sequence('products', 'id'), (SELECT COALESCE(MAX(id), 1) FROM products));
