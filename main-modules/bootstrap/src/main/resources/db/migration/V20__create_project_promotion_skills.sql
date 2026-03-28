CREATE TABLE project_promotion_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    UNIQUE KEY uk_project_promotion_skill (project_id, skill_id),
    INDEX idx_project_promotion_skills_skill (skill_id),
    CONSTRAINT fk_project_promotion_skills_promotion
        FOREIGN KEY (project_id) REFERENCES project_promotions (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_promotion_skills_skill
        FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE
);
