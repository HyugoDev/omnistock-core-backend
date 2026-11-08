-- 1. Insertar Categorías Iniciales
INSERT INTO categories (name, description, active, created_at, updated_at)
VALUES ('Bebidas', 'Refrescos, jugos y aguas', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO categories (name, description, active, created_at, updated_at)
VALUES ('Snacks', 'Papas, galletas y botanas', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO categories (name, description, active, created_at, updated_at)
VALUES ('Lácteos', 'Leche, queso y yogur', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 2. Insertar Productos de Prueba (vinculados a category_id)
INSERT INTO products (barcode, name, description, purchase_price, sale_price, current_stock, min_stock, active, category_id, created_at, updated_at)
VALUES ('7501001', 'Coca Cola 600ml', 'Refresco de cola en botella', 12.00, 18.00, 25, 5, true, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO products (barcode, name, description, purchase_price, sale_price, current_stock, min_stock, active, category_id, created_at, updated_at)
VALUES ('7501002', 'Papas Fritas Sal 45g', 'Papas fritas con sal marina', 10.00, 16.50, 4, 5, true, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
