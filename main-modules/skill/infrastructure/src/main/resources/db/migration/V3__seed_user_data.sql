INSERT INTO users (
    email,
    name,
    nickname,
    password,
    age,
    tel,
    type,
    is_active
) VALUES (
    'test1@sidework.com',
    '홍길동',
    'gildong',
    '$2a$10$abcdefghijklmnopqrstuv', -- bcrypt 더미
    25,
    '010-1234-5678',
    'USER',
    TRUE
);