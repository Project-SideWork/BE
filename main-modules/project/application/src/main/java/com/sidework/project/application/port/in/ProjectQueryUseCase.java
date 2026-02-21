package com.sidework.project.application.port.in;


import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRecruitPosition;

import java.util.List;

public interface ProjectQueryUseCase {
    Project queryById(Long projectId);
    List<Project> queryByUserId(Long userId);
    ProjectDetailResponse queryProjectDetail(Long projectId);
    List<ProjectRecruitPosition> queryProjectRecruitPosition(Long projectId);
}
