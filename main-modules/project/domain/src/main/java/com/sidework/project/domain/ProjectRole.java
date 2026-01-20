package com.sidework.project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectRole {
    OWNER("소유자"),BACKEND("백엔드"), FRONTEND("프론트엔드");

    private final String value;
}
