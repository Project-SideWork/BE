package com.sidework.project.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingDay {
    MON("월"), TUE("화"), WED("수"), THU("목"), FRI("금"), SAT("토"), SUN("일");
    private final String value;
}
