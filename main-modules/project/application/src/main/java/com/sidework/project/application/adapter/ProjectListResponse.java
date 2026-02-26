package com.sidework.project.application.adapter;

import com.sidework.project.domain.ProjectStatus;

import java.util.List;

public record ProjectListResponse(
        Long projectId,
        String title,
        String description,
        ProjectStatus status,
        boolean liked,
        List<ProjectDetailResponse.RecruitPositionResponse> recruitPositions,
        List<String> requiredStacks,
        String creatorName
) {
    public static ProjectListResponse of(
            Long projectId,
            String title,
            String description,
            ProjectStatus status,
            boolean liked,
            List<ProjectDetailResponse.RecruitPositionResponse> recruitPositions,
            List<String> requiredStacks,
            String creatorName) {
        return new ProjectListResponse(
                projectId,
                title,
                description,
                status,
                liked,
                recruitPositions,
                requiredStacks,
                creatorName);
    }
}
