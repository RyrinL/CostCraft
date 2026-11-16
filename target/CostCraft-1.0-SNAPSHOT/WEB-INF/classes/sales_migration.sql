-- Run this once against the existing `costcraft` database to add the Sales module.
USE costcraft;

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
