-- CostCraft Seed Data
USE costcraft;

-- ============================================================
-- CURRENCY RATES
-- ============================================================
INSERT INTO currency_rate (code, name, rate_to_base, decimal_places, symbol) VALUES
('IDR', 'Indonesian Rupiah', 1.00000000, 0, 'Rp '),
('USD', 'US Dollar',         0.00006250, 2, '$'),
('CNY', 'Chinese Yuan',      0.00045500, 2, '¥'),
('EUR', 'Euro',              0.00005820, 2, '€')
ON DUPLICATE KEY UPDATE name=VALUES(name), rate_to_base=VALUES(rate_to_base);

-- ============================================================
-- USERS
-- The OWNER account (admin / admin123) is created automatically by
-- AppContextListener on first deployment if no OWNER exists.
-- Create WORKER accounts via the app UI (Users menu).
-- ============================================================

-- ============================================================
-- MATERIAL CATEGORIES
-- ============================================================
INSERT INTO material_category (name, display_order) VALUES
('Flours',               1),
('Sugars',               2),
('Dairy & Cheese',       3),
('Fats & Butter',        4),
('Eggs',                 5),
('Leaveners & Emulsifiers', 6),
('Flavors & Spices',     7),
('Salt & Acids',         8),
('Nuts & Toppings',      9),
('Other',               10)
ON DUPLICATE KEY UPDATE display_order=VALUES(display_order);

-- ============================================================
-- MATERIALS (~45 rows from bakery spreadsheet)
-- unit_cost = package_price / package_size
-- current_stock = 1 package; low_stock_threshold ≈ 20% of package
-- ============================================================
INSERT INTO material (category_id, name, package_size, package_price, unit_cost, unit, current_stock, low_stock_threshold) VALUES
-- Flours (cat 1)
((SELECT id FROM material_category WHERE name='Flours'), 'All-Purpose Flour',      1000, 14000, 14.0000,  'gram', 1000, 200),
((SELECT id FROM material_category WHERE name='Flours'), 'Mocaf Flour',             500, 12000, 24.0000,  'gram',  500, 100),
((SELECT id FROM material_category WHERE name='Flours'), 'Cake Flour',             1000, 18000, 18.0000,  'gram', 1000, 200),
((SELECT id FROM material_category WHERE name='Flours'), 'Tapioca Starch',          500,  8000, 16.0000,  'gram',  500, 100),
((SELECT id FROM material_category WHERE name='Flours'), 'Cornstarch',              500,  9000, 18.0000,  'gram',  500, 100),
((SELECT id FROM material_category WHERE name='Flours'), 'Oat Flour',               500, 22000, 44.0000,  'gram',  500, 100),

-- Sugars (cat 2)
((SELECT id FROM material_category WHERE name='Sugars'), 'Powdered Sugar',         1000, 16000, 16.0000,  'gram', 1000, 200),
((SELECT id FROM material_category WHERE name='Sugars'), 'Granulated Sugar',       1000, 14000, 14.0000,  'gram', 1000, 200),
((SELECT id FROM material_category WHERE name='Sugars'), 'Brown Sugar',             500, 10000, 20.0000,  'gram',  500, 100),

-- Dairy & Cheese (cat 3)
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Unsalted Butter',  200, 25000, 125.0000, 'gram',  200,  40),
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Margarine',        200,  8000,  40.0000, 'gram',  200,  40),
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Cream Cheese',     250, 45000, 180.0000, 'gram',  250,  50),
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Cheddar Cheese',   180, 32000, 177.7778, 'gram',  180,  36),
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Full Cream Milk Powder', 400, 50000, 125.0000, 'gram', 400, 80),
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Evaporated Milk',  370, 12000,  32.4324, 'ml',    370,  74),
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Parmesan Cheese',  100, 35000, 350.0000, 'gram',  100,  20),
((SELECT id FROM material_category WHERE name='Dairy & Cheese'), 'Edam Cheese',      200, 55000, 275.0000, 'gram',  200,  40),

-- Fats & Butter (cat 4)
((SELECT id FROM material_category WHERE name='Fats & Butter'), 'Vegetable Oil',    1000,  18000,  18.0000, 'ml',  1000, 200),
((SELECT id FROM material_category WHERE name='Fats & Butter'), 'Coconut Oil',       500,  25000,  50.0000, 'ml',   500, 100),

-- Eggs (cat 5)
((SELECT id FROM material_category WHERE name='Eggs'), 'Egg (chicken)',               1,   2500,  2500.0000, 'pcs',  10,   2),
((SELECT id FROM material_category WHERE name='Eggs'), 'Egg Yolk',                    1,   1500,  1500.0000, 'pcs',  10,   2),

-- Leaveners & Emulsifiers (cat 6)
((SELECT id FROM material_category WHERE name='Leaveners & Emulsifiers'), 'Baking Powder',  100,  6000,  60.0000, 'gram', 100, 20),
((SELECT id FROM material_category WHERE name='Leaveners & Emulsifiers'), 'Baking Soda',    200,  5000,  25.0000, 'gram', 200, 40),
((SELECT id FROM material_category WHERE name='Leaveners & Emulsifiers'), 'SP Emulsifier',  100, 12000, 120.0000, 'gram', 100, 20),
((SELECT id FROM material_category WHERE name='Leaveners & Emulsifiers'), 'TBM Emulsifier', 100, 15000, 150.0000, 'gram', 100, 20),

-- Flavors & Spices (cat 7)
((SELECT id FROM material_category WHERE name='Flavors & Spices'), 'Vanilla Extract',  60,  25000, 416.6667, 'ml',   60, 12),
((SELECT id FROM material_category WHERE name='Flavors & Spices'), 'Vanilla Powder',   50,  12000, 240.0000, 'gram', 50, 10),
((SELECT id FROM material_category WHERE name='Flavors & Spices'), 'Pandan Paste',     75,  10000, 133.3333, 'ml',   75, 15),
((SELECT id FROM material_category WHERE name='Flavors & Spices'), 'Cocoa Powder',    200,  25000, 125.0000, 'gram', 200, 40),
((SELECT id FROM material_category WHERE name='Flavors & Spices'), 'Dark Chocolate',   200, 40000, 200.0000, 'gram', 200, 40),
((SELECT id FROM material_category WHERE name='Flavors & Spices'), 'Green Tea Powder', 100, 55000, 550.0000, 'gram', 100, 20),
((SELECT id FROM material_category WHERE name='Flavors & Spices'), 'Red Velvet Paste',  75, 15000, 200.0000, 'ml',   75, 15),

-- Salt & Acids (cat 8)
((SELECT id FROM material_category WHERE name='Salt & Acids'), 'Fine Salt',          500,   5000,  10.0000, 'gram', 500, 100),
((SELECT id FROM material_category WHERE name='Salt & Acids'), 'Lemon Juice',        250,  15000,  60.0000, 'ml',   250,  50),
((SELECT id FROM material_category WHERE name='Salt & Acids'), 'Lemon Zest',           1,   3000,  3000.000, 'pcs',   5,   1),

-- Nuts & Toppings (cat 9)
((SELECT id FROM material_category WHERE name='Nuts & Toppings'), 'Cashew Nuts',         250, 45000, 180.0000, 'gram', 250, 50),
((SELECT id FROM material_category WHERE name='Nuts & Toppings'), 'Almond Slices',       100, 35000, 350.0000, 'gram', 100, 20),
((SELECT id FROM material_category WHERE name='Nuts & Toppings'), 'Chocolate Chips',     200, 32000, 160.0000, 'gram', 200, 40),
((SELECT id FROM material_category WHERE name='Nuts & Toppings'), 'Rolled Oats',         500, 20000,  40.0000, 'gram', 500, 100),
((SELECT id FROM material_category WHERE name='Nuts & Toppings'), 'Desiccated Coconut',  200, 12000,  60.0000, 'gram', 200, 40),

-- Other (cat 10)
((SELECT id FROM material_category WHERE name='Other'), 'Jar (container)',           1,   6000, 6000.0000, 'pcs',   50,  10),
((SELECT id FROM material_category WHERE name='Other'), 'Lid',                       1,    500,  500.0000, 'pcs',   50,  10),
((SELECT id FROM material_category WHERE name='Other'), 'Baking Paper',              1,    800,  800.0000, 'pcs',   50,  10),
((SELECT id FROM material_category WHERE name='Other'), 'Sticker Label',             1,    500,  500.0000, 'pcs',  100,  20);

-- ============================================================
-- OVERHEAD CONFIGURATION
-- Stage 2: DIRECT_OTHER
-- Stage 3: PERCENTAGE_OVERHEAD
-- ============================================================
INSERT INTO overhead_config (name, cost_stage, allocation_rule, fixed_amount, daily_amount, days_per_month, percentage, active, display_order) VALUES
-- Stage 2
('Jar',           'DIRECT_OTHER', 'PER_BATCH_FIXED', 6000,    NULL, 30, NULL, TRUE, 1),
('Lid',           'DIRECT_OTHER', 'PER_BATCH_FIXED',   80,    NULL, 30, NULL, TRUE, 2),
('Baking Paper',  'DIRECT_OTHER', 'PER_BATCH_FIXED', 1000,    NULL, 30, NULL, TRUE, 3),
('Sticker',       'DIRECT_OTHER', 'PER_BATCH_FIXED', 1500,    NULL, 30, NULL, TRUE, 4),
('Water',         'DIRECT_OTHER', 'PER_BATCH_FIXED', 2000,    NULL, 30, NULL, TRUE, 5),
('Electricity',   'DIRECT_OTHER', 'DAILY_ALLOCATED', NULL, 1000000, 30, NULL, TRUE, 6),
-- Stage 3
('Operational Cost', 'PERCENTAGE_OVERHEAD', 'PERCENTAGE_OF_DIRECT', NULL, NULL, 30, 0.20, TRUE, 1),
('Labor Cost',       'PERCENTAGE_OVERHEAD', 'PERCENTAGE_OF_DIRECT', NULL, NULL, 30, 0.25, TRUE, 2),
('Marketing Cost',   'PERCENTAGE_OVERHEAD', 'PERCENTAGE_OF_DIRECT', NULL, NULL, 30, 0.20, TRUE, 3),
('Risk Buffer',      'PERCENTAGE_OVERHEAD', 'PERCENTAGE_OF_DIRECT', NULL, NULL, 30, 0.10, TRUE, 4);

-- ============================================================
-- PRODUCTS (5 demo products from the bakery)
-- ============================================================
INSERT INTO product (name, yield_per_batch, days_per_batch, margin_lower_pct, margin_upper_pct, active) VALUES
('Nastar',                30, 0.5, 0.30, 0.50, TRUE),
('Kastengle',             25, 0.5, 0.30, 0.50, TRUE),
('Putri Salju',           35, 0.5, 0.30, 0.50, TRUE),
('Palm Cheese',           30, 0.5, 0.30, 0.50, TRUE),
('Chocolate Cashew',      28, 0.5, 0.30, 0.50, TRUE)
ON DUPLICATE KEY UPDATE yield_per_batch=VALUES(yield_per_batch);

-- ============================================================
-- BOMs — approximate recipes (grams unless noted)
-- ============================================================

-- Nastar (pineapple jam cookies)
INSERT IGNORE INTO bom_item (product_id, material_id, gram_quantity, display_order)
SELECT p.id, m.id, qty, ord FROM product p, (
    SELECT 'All-Purpose Flour' AS mat, 250 AS qty, 1 AS ord UNION ALL
    SELECT 'Powdered Sugar', 60, 2 UNION ALL
    SELECT 'Unsalted Butter', 200, 3 UNION ALL
    SELECT 'Egg Yolk', 3, 4 UNION ALL
    SELECT 'Vanilla Powder', 5, 5 UNION ALL
    SELECT 'Fine Salt', 2, 6
) src JOIN material m ON m.name = src.mat
WHERE p.name = 'Nastar';

-- Kastengle (cheese sticks)
INSERT IGNORE INTO bom_item (product_id, material_id, gram_quantity, display_order)
SELECT p.id, m.id, qty, ord FROM product p, (
    SELECT 'All-Purpose Flour' AS mat, 200 AS qty, 1 AS ord UNION ALL
    SELECT 'Margarine', 100, 2 UNION ALL
    SELECT 'Cheddar Cheese', 100, 3 UNION ALL
    SELECT 'Edam Cheese', 50, 4 UNION ALL
    SELECT 'Egg Yolk', 2, 5 UNION ALL
    SELECT 'Fine Salt', 3, 6
) src JOIN material m ON m.name = src.mat
WHERE p.name = 'Kastengle';

-- Putri Salju (snowball cookies)
INSERT IGNORE INTO bom_item (product_id, material_id, gram_quantity, display_order)
SELECT p.id, m.id, qty, ord FROM product p, (
    SELECT 'All-Purpose Flour' AS mat, 225 AS qty, 1 AS ord UNION ALL
    SELECT 'Powdered Sugar', 50, 2 UNION ALL
    SELECT 'Unsalted Butter', 150, 3 UNION ALL
    SELECT 'Cashew Nuts', 75, 4 UNION ALL
    SELECT 'Cornstarch', 25, 5 UNION ALL
    SELECT 'Vanilla Powder', 3, 6
) src JOIN material m ON m.name = src.mat
WHERE p.name = 'Putri Salju';

-- Palm Cheese (palm-sugar cheese cookies)
INSERT IGNORE INTO bom_item (product_id, material_id, gram_quantity, display_order)
SELECT p.id, m.id, qty, ord FROM product p, (
    SELECT 'All-Purpose Flour' AS mat, 200 AS qty, 1 AS ord UNION ALL
    SELECT 'Parmesan Cheese', 50, 2 UNION ALL
    SELECT 'Cheddar Cheese', 50, 3 UNION ALL
    SELECT 'Unsalted Butter', 125, 4 UNION ALL
    SELECT 'Powdered Sugar', 40, 5 UNION ALL
    SELECT 'Egg Yolk', 2, 6 UNION ALL
    SELECT 'Fine Salt', 2, 7
) src JOIN material m ON m.name = src.mat
WHERE p.name = 'Palm Cheese';

-- Chocolate Cashew
INSERT IGNORE INTO bom_item (product_id, material_id, gram_quantity, display_order)
SELECT p.id, m.id, qty, ord FROM product p, (
    SELECT 'All-Purpose Flour' AS mat, 200 AS qty, 1 AS ord UNION ALL
    SELECT 'Cocoa Powder', 30, 2 UNION ALL
    SELECT 'Powdered Sugar', 80, 3 UNION ALL
    SELECT 'Unsalted Butter', 150, 4 UNION ALL
    SELECT 'Cashew Nuts', 100, 5 UNION ALL
    SELECT 'Egg Yolk', 2, 6 UNION ALL
    SELECT 'Vanilla Powder', 3, 7 UNION ALL
    SELECT 'Fine Salt', 2, 8
) src JOIN material m ON m.name = src.mat
WHERE p.name = 'Chocolate Cashew';

-- ============================================================
-- HISTORICAL BATCHES
-- Run after creating worker accounts via the app UI.
-- Replace 'siti' and 'budi' with actual worker usernames.
-- ============================================================
-- Example (run manually after creating workers):
-- INSERT INTO production_batch (...) SELECT p.id, 1.0, ... FROM product p, user u WHERE ...

