-- profiles: 자기소개, 거주지 컬럼 추가
ALTER TABLE profiles
    ADD COLUMN self_introduction TEXT NULL AFTER user_id,
    ADD COLUMN residence VARCHAR(20) NULL AFTER self_introduction;

-- portfolios: 소속/기관명 컬럼 추가 (기존 행 허용을 위해 NULL 허용)
ALTER TABLE portfolios
    ADD COLUMN organization_name VARCHAR(30) NULL AFTER content;

-- profile_skills: 숙련도 컬럼 추가
ALTER TABLE profile_skills
    ADD COLUMN proficiency VARCHAR(30) NULL AFTER skill_id;
