package com.sidework.project.application.port.in;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.application.adapter.ProjectListResponse;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.domain.ProjectRole;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProjectQueryUseCase {
    Project queryById(Long projectId);
    List<Project> queryByUserId(Long userId);
    ProjectDetailResponse queryProjectDetail(Long projectId);
    List<ProjectRecruitPosition> queryProjectRecruitPosition(Long projectId);
    Map<Long, List<ProjectRole>> queryUserRolesByProjects(Long userId, List<Long> projectIds);
    PageResponse<List<ProjectListResponse>> queryProjectList(Long userId, Pageable pageable);
}
