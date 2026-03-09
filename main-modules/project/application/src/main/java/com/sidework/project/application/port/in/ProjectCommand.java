package com.sidework.project.application.port.in;

import com.sidework.project.domain.*;
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

        @NotNull
        LocalDate startDt,

        @NotNull
        LocalDate endDt,

        @NotNull
        MeetingType meetingType,

        @NotNull @Positive Long meetRegionId,

        @Valid
        List<ProjectScheduleCommand> meetingSchedules,

        @NotEmpty
        List<Long> requiredStacks,

        List<Long> preferredStacks,

        @NotNull
        ProjectStatus status
) {}