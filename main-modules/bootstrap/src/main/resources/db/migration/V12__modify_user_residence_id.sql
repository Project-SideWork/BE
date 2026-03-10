ALTER TABLE users
ADD COLUMN residence_region_id BIGINT NULL,
ADD INDEX idx_residence_region_id (residence_region_id),
ADD CONSTRAINT fk_users_residence_region
FOREIGN KEY (residence_region_id) REFERENCES regions(id);
