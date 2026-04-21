CREATE TABLE IF NOT EXISTS credits (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount INT NOT NULL,
    remaining_amount INT NOT NULL,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_credits_amount_non_negative CHECK (amount >= 0),
    CONSTRAINT chk_credits_remaining_non_negative CHECK (remaining_amount >= 0),
    CONSTRAINT chk_credits_remaining_le_amount CHECK (remaining_amount <= amount)
);