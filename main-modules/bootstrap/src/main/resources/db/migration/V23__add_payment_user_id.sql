ALTER TABLE payments
ADD COLUMN user_id BIGINT NULL AFTER payment_id,
ADD INDEX idx_user_id (user_id);