package com.sidework.project.application.port.in;

import com.sidework.project.domain.MeetingDay;
import com.sidework.project.domain.MeetingHour;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProjectScheduleCommand(
        @NotNull(message = "요일은 필수입니다.") MeetingDay day,
        @NotEmpty(message = "시간을 최소 1개 이상 선택해주세요.")
        List<@NotNull(message = "시간은 null일 수 없습니다.") MeetingHour> hours
) {
}
