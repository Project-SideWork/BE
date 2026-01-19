package com.sidework.project.application.port.in;

import com.sidework.project.domain.ProjectRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RecruitPosition(
        @NotNull
        ProjectRole role,       // 프론트엔드, 백엔드 등

        @NotNull
        @Min(value = 1, message = "모집 인원은 0보다 커야합니다.")
        Integer headCount, // 인원 수

        @NotNull
        SkillLevel level   // 초급 / 중급 / 고급
) {}