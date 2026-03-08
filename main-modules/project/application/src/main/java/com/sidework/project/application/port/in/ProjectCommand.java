package com.sidework.project.application.port.in;

import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

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
        List<@Valid RecruitPosition> recruitPositions,

        @NotNull @Positive Long residenceRegionId,

        @NotNull
        LocalDate startDt,

        @NotNull
        LocalDate endDt,

        @NotNull
        MeetingType meetingType,

        String meetingDetail,

        @NotEmpty
        List<Long> requiredStacks,

        List<Long> preferredStacks,

        @NotNull
        ProjectStatus status
) {}