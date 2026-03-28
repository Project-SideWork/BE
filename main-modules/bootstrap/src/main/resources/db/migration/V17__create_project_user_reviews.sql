CREATE TABLE project_user_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    reviewer_user_id BIGINT NOT NULL,
    reviewee_user_id BIGINT NOT NULL,
    responsibility TINYINT NOT NULL,
    communication TINYINT NOT NULL,
    collaboration TINYINT NOT NULL,
    problem_solving TINYINT NOT NULL,
    comment VARCHAR(1000),
    UNIQUE KEY uk_project_user_review (project_id, reviewer_user_id, reviewee_user_id),
    INDEX idx_project_user_review_project (project_id),
    INDEX idx_project_user_review_reviewer (reviewer_user_id),
    INDEX idx_project_user_review_reviewee (reviewee_user_id)
);

