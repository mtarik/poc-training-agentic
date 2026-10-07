-- Jeu de données de démonstration (rechargé à chaque démarrage)

INSERT INTO product (id, sku, name, category, price, promo_percent, weight_grams, stock) VALUES
(1, 'BOO-0001', 'Clean Code', 'BOOKS', 32.50, NULL, 600, 25),
(2, 'BOO-0002', 'Domain-Driven Design', 'BOOKS', 54.90, NULL, 1100, 10),
(3, 'BOO-0003', 'Effective Java', 'BOOKS', 45.00, 20, 800, 15),
(4, 'ELE-0001', 'Clavier mécanique', 'ELECTRONICS', 89.90, NULL, 1200, 8),
(5, 'ELE-0002', 'Casque audio', 'ELECTRONICS', 129.00, 15, 350, 5),
(6, 'ELE-0003', 'Ecran 27 pouces', 'ELECTRONICS', 279.00, NULL, 6500, 3),
(7, 'HOM-0001', 'Lampe de bureau', 'HOME', 39.90, NULL, 1500, 20),
(8, 'HOM-0002', 'Mug développeur', 'HOME', 12.00, 50, 400, 100),
(9, 'TOY-0001', 'Puzzle 1000 pièces', 'TOYS', 19.90, NULL, 900, 30),
(10, 'TOY-0002', 'Jeu de société Agile', 'TOYS', 34.00, NULL, 1300, 0);

INSERT INTO customer (id, email, first_name, last_name, loyalty_level, internal_notes, created_at) VALUES
(1, 'alice.martin@example.com', 'Alice', 'Martin', 'GOLD', 'Client VIP - geste commercial accordé le 12/03', '2024-01-15 10:00:00'),
(2, 'bob.durand@example.com', 'Bob', 'Durand', 'STANDARD', 'A menacé de porter plainte, ne pas relancer', '2024-03-02 14:30:00'),
(3, 'chloe.bernard@example.com', 'Chloé', 'Bernard', 'STANDARD', NULL, '2024-06-20 09:15:00'),
(4, 'david.petit@example.com', 'David', 'Petit', 'GOLD', 'Carte bancaire refusée 2 fois', '2025-02-11 17:45:00');

INSERT INTO orders (id, customer_id, status, country, express, total_weight_grams, subtotal, shipping_cost, total, created_at) VALUES
(1, 2, 'SHIPPED', 'FR', FALSE, 600, 32.50, 4.90, 37.40, '2026-09-01 10:00:00'),
(2, 1, 'PAID', 'FR', FALSE, 1200, 85.41, 0.00, 85.41, '2026-09-03 11:20:00'),
(3, 3, 'CREATED', 'BE', FALSE, 2000, 74.80, 9.90, 84.70, '2026-09-05 08:45:00'),
(4, 4, 'SHIPPED', 'FR', TRUE, 6500, 265.05, 9.90, 274.95, '2026-09-08 16:10:00'),
(5, 2, 'CREATED', 'US', FALSE, 3000, 79.80, 23.90, 103.70, '2026-09-12 19:30:00'),
(6, 3, 'PAID', 'FR', FALSE, 1800, 39.80, 4.90, 44.70, '2026-09-15 12:00:00'),
(7, 1, 'SHIPPED', 'DE', FALSE, 1700, 83.03, 4.95, 87.98, '2026-09-20 09:05:00'),
(8, 4, 'CREATED', 'FR', FALSE, 1500, 37.91, 2.45, 40.36, '2026-09-28 15:40:00');

INSERT INTO order_line (id, order_id, product_id, quantity, unit_price, line_total) VALUES
(1, 1, 1, 1, 32.50, 32.50),
(2, 2, 4, 1, 89.90, 89.90),
(3, 3, 2, 1, 54.90, 54.90),
(4, 3, 9, 1, 19.90, 19.90),
(5, 4, 6, 1, 279.00, 279.00),
(6, 5, 7, 2, 39.90, 79.80),
(7, 6, 9, 2, 19.90, 39.80),
(8, 7, 1, 1, 32.50, 32.50),
(9, 7, 2, 1, 54.90, 54.90),
(10, 8, 7, 1, 39.90, 39.90);

-- Les ids ci-dessus sont explicites : on repositionne les séquences pour les insertions suivantes.
ALTER TABLE product ALTER COLUMN id RESTART WITH 100;
ALTER TABLE customer ALTER COLUMN id RESTART WITH 100;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 100;
ALTER TABLE order_line ALTER COLUMN id RESTART WITH 100;
