CREATE TABLE profile_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_profile_like (user_id, profile_id),
    INDEX idx_profile_like_user (user_id),
    INDEX idx_profile_like_profile (profile_id)
);

