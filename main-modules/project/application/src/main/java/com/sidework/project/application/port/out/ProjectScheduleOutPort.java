package com.sidework.project.application.port.out;

import com.sidework.project.domain.ProjectSchedule;

import java.util.List;

public interface ProjectScheduleOutPort {
    void saveAll(List<ProjectSchedule> schedules);
}
