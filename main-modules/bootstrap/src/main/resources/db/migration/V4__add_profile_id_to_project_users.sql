ALTER TABLE project_users
    ADD COLUMN profile_id BIGINT NULL AFTER user_id,
    ADD INDEX idx_profile_id (profile_id);
