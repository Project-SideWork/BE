CREATE TABLE project_retrospectives (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_description TEXT NOT NULL,
    strengths TEXT NOT NULL,
    improvements TEXT NOT NULL,

    UNIQUE KEY uk_project_retrospective (project_id, user_id),
    INDEX idx_project_retrospectives_project (project_id),
    INDEX idx_project_retrospectives_user (user_id),

    CONSTRAINT fk_project_retrospectives_project
        FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_retrospectives_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
