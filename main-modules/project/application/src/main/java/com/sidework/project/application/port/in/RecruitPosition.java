package com.sidework.project.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecruitPosition(
        @NotBlank
        RecruitRole role,       // 프론트엔드, 백엔드 등

        @NotNull
        Integer headCount, // 인원 수

        @NotNull
        SkillLevel level   // 초급 / 중급 / 고급
) {}