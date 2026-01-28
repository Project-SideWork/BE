-- 기본 스킬 데이터 (id는 AUTO_INCREMENT로 자동 생성하는 게 좋지만, 요청대로 작성)

-- Backend
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(1, 'Java', NOW(6), NOW(6)),
(2, 'Spring', NOW(6), NOW(6)),
(3, 'Java Spring Boot', NOW(6), NOW(6)),
(4, 'Node.js', NOW(6), NOW(6)),
(5, 'Express', NOW(6), NOW(6)),
(6, 'Python', NOW(6), NOW(6)),
(7, 'Django', NOW(6), NOW(6)),
(8, 'FastAPI', NOW(6), NOW(6)),
(9, 'Kotlin Spring Boot', NOW(6), NOW(6)),
(10, 'Go', NOW(6), NOW(6));

-- Frontend
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(11, 'JavaScript', NOW(6), NOW(6)),
(12, 'TypeScript', NOW(6), NOW(6)),
(13, 'React', NOW(6), NOW(6)),
(14, 'Vue.js', NOW(6), NOW(6)),
(15, 'Angular', NOW(6), NOW(6)),
(16, 'Next.js', NOW(6), NOW(6)),
(17, 'Svelte', NOW(6), NOW(6)),
(18, 'HTML', NOW(6), NOW(6)),
(19, 'CSS', NOW(6), NOW(6)),
(20, 'Tailwind CSS', NOW(6), NOW(6));

-- Mobile
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(21, 'React Native', NOW(6), NOW(6)),
(22, 'Flutter', NOW(6), NOW(6)),
(23, 'Swift', NOW(6), NOW(6)),
(24, 'Kotlin', NOW(6), NOW(6)),
(25, 'Android', NOW(6), NOW(6)),
(26, 'iOS', NOW(6), NOW(6));

-- Database
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(27, 'MySQL', NOW(6), NOW(6)),
(28, 'PostgreSQL', NOW(6), NOW(6)),
(29, 'MongoDB', NOW(6), NOW(6)),
(30, 'Redis', NOW(6), NOW(6)),
(31, 'Oracle', NOW(6), NOW(6)),
(32, 'MariaDB', NOW(6), NOW(6)),
(33, 'SQLite', NOW(6), NOW(6)),
(34, 'DynamoDB', NOW(6), NOW(6));

-- DevOps
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(35, 'Docker', NOW(6), NOW(6)),
(36, 'Kubernetes', NOW(6), NOW(6)),
(37, 'AWS', NOW(6), NOW(6)),
(38, 'GCP', NOW(6), NOW(6)),
(39, 'Azure', NOW(6), NOW(6)),
(40, 'Jenkins', NOW(6), NOW(6)),
(41, 'GitHub Actions', NOW(6), NOW(6)),
(42, 'GitLab CI/CD', NOW(6), NOW(6)),
(43, 'Terraform', NOW(6), NOW(6)),
(44, 'Ansible', NOW(6), NOW(6));

-- Tools & Others
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(45, 'Git', NOW(6), NOW(6)),
(46, 'GitHub', NOW(6), NOW(6)),
(47, 'GitLab', NOW(6), NOW(6)),
(48, 'Jira', NOW(6), NOW(6)),
(49, 'Figma', NOW(6), NOW(6)),
(50, 'Postman', NOW(6), NOW(6));

-- Message Queue & Cache
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(51, 'Kafka', NOW(6), NOW(6)),
(52, 'RabbitMQ', NOW(6), NOW(6)),
(53, 'Memcached', NOW(6), NOW(6));

-- Testing
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(54, 'JUnit', NOW(6), NOW(6)),
(55, 'Jest', NOW(6), NOW(6)),
(56, 'Cypress', NOW(6), NOW(6)),
(57, 'Selenium', NOW(6), NOW(6));

-- AI/ML (추가)
INSERT INTO skills (id, name, created_at, updated_at) VALUES 
(58, 'TensorFlow', NOW(6), NOW(6)),
(59, 'PyTorch', NOW(6), NOW(6)),
(60, 'scikit-learn', NOW(6), NOW(6)),
(61, 'Pandas', NOW(6), NOW(6)),
(62, 'NumPy', NOW(6), NOW(6));