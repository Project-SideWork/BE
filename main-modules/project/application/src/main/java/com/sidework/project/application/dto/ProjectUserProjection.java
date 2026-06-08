package com.sidework.project.application.dto;

import com.sidework.project.domain.ProjectRole;

public record ProjectUserProjection(
        Long userId, String nickname, Long profileId, ProjectRole role
) {
}