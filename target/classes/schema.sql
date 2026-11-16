-- CostCraft Database Schema
-- MySQL 8 / InnoDB / utf8mb4

CREATE DATABASE IF NOT EXISTS costcraft
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE costcraft;

-- 1. user
CREATE TABLE IF NOT EXISTS user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(64)  NOT NULL,
    salt            VARCHAR(32)  NOT NULL,
    role            ENUM('OWNER','WORKER') NOT NULL,
    email           VARCHAR(100),
    display_currency VARCHAR(3)  NOT NULL DEFAULT 'IDR',
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. currency_rate
CREATE TABLE IF NOT EXISTS currency_rate (
    code            VARCHAR(3)   PRIMARY KEY,
    name            VARCHAR(50)  NOT NULL,
    rate_to_base    DECIMAL(18,8) NOT NULL,
    decimal_places  INT          NOT NULL DEFAULT 2,
    symbol          VARCHAR(8)   NOT NULL,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. material_category
CREATE TABLE IF NOT EXISTS material_category (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50)  NOT NULL UNIQUE,
    display_order   INT          DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. material
CREATE TABLE IF NOT EXISTS material (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id         BIGINT       NOT NULL,
    name                VARCHAR(100) NOT NULL,
    package_size        DECIMAL(10,2) NOT NULL,
    package_price       DECIMAL(12,2) NOT NULL,
    unit_cost           DECIMAL(12,4) NOT NULL,
    unit                VARCHAR(20)  NOT NULL DEFAULT 'gram',
    current_stock       DECIMAL(12,2) NOT NULL DEFAULT 0,
    low_stock_threshold DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at          DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES material_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. product
CREATE TABLE IF NOT EXISTS product (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    yield_per_batch     DECIMAL(6,2) NOT NULL DEFAULT 1,
    days_per_batch      DECIMAL(5,2) NOT NULL DEFAULT 0.5,
    margin_lower_pct    DECIMAL(5,4) NOT NULL DEFAULT 0.30,
    margin_upper_pct    DECIMAL(5,4) NOT NULL DEFAULT 0.50,
    cached_total_direct DECIMAL(12,2) DEFAULT 0,
    cached_unit_cost    DECIMAL(12,2) DEFAULT 0,
    cached_price_lower  DECIMAL(12,2) DEFAULT 0,
    cached_price_upper  DECIMAL(12,2) DEFAULT 0,
    active              BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. bom_item
CREATE TABLE IF NOT EXISTS bom_item (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT       NOT NULL,
    material_id     BIGINT       NOT NULL,
    gram_quantity   DECIMAL(10,2) NOT NULL,
    display_order   INT          DEFAULT 0,
    FOREIGN KEY (product_id)  REFERENCES product(id)  ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES material(id),
    UNIQUE KEY uk_product_material (product_id, material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. overhead_config
CREATE TABLE IF NOT EXISTS overhead_config (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    cost_stage      ENUM('DIRECT_OTHER','PERCENTAGE_OVERHEAD') NOT NULL,
    allocation_rule ENUM('PER_BATCH_FIXED','PER_UNIT_FIXED','DAILY_ALLOCATED','PERCENTAGE_OF_DIRECT') NOT NULL,
    fixed_amount    DECIMAL(12,2),
    daily_amount    DECIMAL(12,2),
    days_per_month  INT          DEFAULT 30,
    percentage      DECIMAL(5,4),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    display_order   INT          DEFAULT 0,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. production_batch
CREATE TABLE IF NOT EXISTS production_batch (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id          BIGINT       NOT NULL,
    multiplier          DECIMAL(6,2) NOT NULL DEFAULT 1,
    units_produced      DECIMAL(8,2) NOT NULL,
    recorded_unit_cost  DECIMAL(12,2) NOT NULL,
    recorded_total_cost DECIMAL(12,2) NOT NULL,
    worker_id           BIGINT       NOT NULL,
    notes               TEXT,
    created_at          DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (worker_id)  REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. batch_item
CREATE TABLE IF NOT EXISTS batch_item (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id            BIGINT       NOT NULL,
    material_id         BIGINT       NOT NULL,
    gram_quantity       DECIMAL(12,2) NOT NULL,
    unit_cost_snapshot  DECIMAL(12,4) NOT NULL,
    line_cost           DECIMAL(12,2) NOT NULL,
    overridden          BOOLEAN      NOT NULL DEFAULT FALSE,
    FOREIGN KEY (batch_id)    REFERENCES production_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES material(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. inventory_log
CREATE TABLE IF NOT EXISTS inventory_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id     BIGINT       NOT NULL,
    change_quantity DECIMAL(12,2) NOT NULL,
    reason          ENUM('PURCHASE','BATCH_CONSUMPTION','ADJUSTMENT','WASTE','INITIAL_STOCK') NOT NULL,
    reference_type  VARCHAR(50),
    reference_id    BIGINT,
    note            VARCHAR(255),
    user_id         BIGINT       NOT NULL,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES material(id),
    FOREIGN KEY (user_id)     REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. alert
CREATE TABLE IF NOT EXISTS alert (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id     BIGINT       NOT NULL,
    alert_type      ENUM('LOW_STOCK') NOT NULL,
    message         VARCHAR(255),
    resolved        BOOLEAN      NOT NULL DEFAULT FALSE,
    resolved_at     DATETIME,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES material(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. sales_order  (an order for an event/festival/customer)
CREATE TABLE IF NOT EXISTS sales_order (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name    VARCHAR(150)  NOT NULL,
    order_date       DATE          NOT NULL,
    note             VARCHAR(255),
    recorded_revenue DECIMAL(14,2) NOT NULL DEFAULT 0,
    recorded_cost    DECIMAL(14,2) NOT NULL DEFAULT 0,
    user_id          BIGINT        NOT NULL,
    created_at       DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. sales_order_item  (a product line within an order)
CREATE TABLE IF NOT EXISTS sales_order_item (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id           BIGINT        NOT NULL,
    product_id         BIGINT        NOT NULL,
    quantity           DECIMAL(10,2) NOT NULL,
    unit_price         DECIMAL(12,2) NOT NULL,
    unit_cost_snapshot DECIMAL(12,2) NOT NULL,
    line_revenue       DECIMAL(14,2) NOT NULL,
    line_cost          DECIMAL(14,2) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES sales_order(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
