package com.sidework.project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplyStatus {
    UNREAD("미열람"), READ("열람"), ACCEPTED("합격"), REJECTED("불합격");
    private final String value;

}
