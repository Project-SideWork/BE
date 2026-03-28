CREATE TABLE project_user_review_stats (
    user_id BIGINT PRIMARY KEY,
    rating_score DOUBLE NOT NULL DEFAULT 0,
    rating_count BIGINT NOT NULL DEFAULT 0,
    INDEX idx_project_user_review_stats_count (rating_count)
);

