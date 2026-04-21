package com.sidework.profile.application.adapter;

import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;

import java.time.LocalDate;
import java.util.List;

public record UserProjectDto(Long projectId,
                             String title,
                             String description,
                             LocalDate startDate,
                             LocalDate endDate,
                             MeetingType meetingType,
                             ProjectStatus status,
                             List<String> projectStacks,
                             List<ProjectRole> role){
}
