package com.sidework.project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingType {
    OFFLINE("대면"), ONLINE("비대면"), HYBRID("혼합");
    private final String value;
}
