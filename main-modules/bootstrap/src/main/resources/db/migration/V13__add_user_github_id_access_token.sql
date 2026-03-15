ALTER TABLE users
ADD COLUMN github_id BIGINT,
ADD COLUMN github_access_token VARCHAR(255);