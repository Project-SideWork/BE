package com.sidework.project.application.adapter;

import com.sidework.project.domain.ProjectStatus;

import java.util.List;

public record ProjectListResponse(
        Long projectId,
        String title,
        String description,
        ProjectStatus status,
        Integer remainingDays,
        boolean liked,
        List<ProjectDetailResponse.RecruitPositionResponse> recruitPositions,
        List<String> requiredStacks,
        String creatorName,
        int durationMonths
) {
}
