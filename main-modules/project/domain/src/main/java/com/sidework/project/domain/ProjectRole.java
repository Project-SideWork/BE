package com.sidework.project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectRole {
    OWNER("소유자"),
    BACKEND("백엔드"),
    FRONTEND("프론트엔드"),
    FULLSTACK("풀스택"),
    MOBILE("모바일"),
    ANDROID("안드로이드"),
    IOS("iOS"),
    DEVOPS("DevOps"),
    INFRA("인프라"),
    CLOUD("클라우드"),
    DATA_ENGINEER("데이터 엔지니어"),
    AI_ENGINEER("AI 엔지니어"),
    ML_ENGINEER("머신러닝 엔지니어"),
    DATA_ANALYST("데이터 분석가"),
    DATA_SCIENTIST("데이터 사이언티스트"),
    SECURITY("보안"),
    BLOCKCHAIN("블록체인"),
    EMBEDDED("임베디드"),
    PM("프로젝트 매니저"),
    PO("프로덕트 오너"),
    PLANNER("기획자"),
    SERVICE_PLANNER("서비스 기획자"),
    DESIGNER("디자이너"),
    UI_UX_DESIGNER("UI/UX 디자이너"),
    PRODUCT_DESIGNER("프로덕트 디자이너"),
    QA("QA"),
    TEST_ENGINEER("테스트 엔지니어"),
    MARKETER("마케터"),
    TECH_WRITER("테크니컬 라이터"),
    ETC("기타");

    private final String value;
}