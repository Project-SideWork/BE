package com.sidework.project.application.port.out;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.domain.Project;

import java.util.List;

public interface ProjectOutPort {
    boolean existsById(Long projectId);
    Long save(Project project);
    Project findById(Long id);
    List<Project> findByIdIn(List<Long> ids);
    List<ProjectTitleDto> findAllTitles(List<Long> projectIds);
}
