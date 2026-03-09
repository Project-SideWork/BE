-- Region 테이블
CREATE TABLE IF NOT EXISTS regions (
    id BIGINT PRIMARY KEY,
    region_name VARCHAR(15) NOT NULL,
    parent_region_id BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
