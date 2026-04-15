CREATE TABLE IF NOT EXISTS payment_reservations (
    payment_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    approved_credit INT NOT NULL,
    status VARCHAR(10) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT pk_payment_reservations PRIMARY KEY (payment_id)
);