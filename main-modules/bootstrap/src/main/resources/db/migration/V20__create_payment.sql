CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(100) PRIMARY KEY,

    transaction_id VARCHAR(100),
    store_id VARCHAR(100),

    order_name VARCHAR(255),

    amount BIGINT NOT NULL,

    currency VARCHAR(10),
    status VARCHAR(50),

    customer_name VARCHAR(100),
    customer_email VARCHAR(150),
    customer_phone VARCHAR(50),

    item_id VARCHAR(100),

    paid_at TIMESTAMP,
    requested_at TIMESTAMP,
    updated_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    INDEX idx_payments_store_id (store_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_paid_at (paid_at)
);