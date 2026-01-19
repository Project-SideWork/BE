package com.sidework.project.application.port.in;

import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ProjectCommand(

        @NotBlank
        @Size(max = 100)
        String title,

        @NotBlank
        String description,

        @NotNull
        ProjectRole myRole,

        @NotNull
        List<RecruitPosition> recruitPositions,

        @NotNull
        LocalDate startDt,

        @NotNull
        LocalDate endDt,

        @NotNull
        MeetingType meetingType,

        String meetingDetail,

        @NotNull
        List<String> requiredStacks,

        List<String> preferredStacks,

        @NotNull
        ProjectStatus status
) {}