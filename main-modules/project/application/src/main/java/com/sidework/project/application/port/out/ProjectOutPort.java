package com.sidework.project.application.port.out;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRecruitPosition;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectOutPort {
    boolean existsById(Long projectId);
    Long save(Project project);
    Project findById(Long id);
    List<Project> findByIdIn(List<Long> ids);
    List<ProjectTitleDto> findAllTitles(List<Long> projectIds);
    Page<Project> findPage(Pageable pageable);
    Map<Long, List<ProjectRecruitPosition>> getProjectRecruitPositionsByProjectIds(List<Long> projectIds);
}
