package com.sidework.project.application.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitRole {
    BACKEND("백엔드"), FRONTEND("프론트엔드");

    private final String value;
}
