package com.sidework.project.application.port.in;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.MyProjectSummaryResponse;
import com.sidework.project.application.adapter.ProjectApplicantResponse;
import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.application.adapter.ProjectListResponse;
import com.sidework.project.application.dto.ProjectIdTitleProjection;
import com.sidework.project.application.dto.ProjectUserReviewStatSummary;
import com.sidework.project.application.dto.ProjectUserReviewSummary;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectUserReview;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProjectQueryUseCase {
    Project queryById(Long projectId);
    List<Project> pageByUserId(Long userId, Pageable pageable);
    ProjectDetailResponse queryProjectDetail(Long userId, Long projectId);
    List<ProjectRecruitPosition> queryProjectRecruitPosition(Long projectId);
    Map<Long, List<ProjectRole>> queryUserRolesByProjects(Long userId, List<Long> projectIds);
    PageResponse<List<ProjectListResponse>> queryProjectList(Long userId, Pageable pageable);
    PageResponse<List<ProjectListResponse>> queryProjectList(Long userId,
                                                             String keyword,
                                                             List<Long> skillIds,
                                                             List<ProjectRole> positions,
                                                             Pageable pageable);
	PageResponse<List<ProjectListResponse>> queryLikedProjectList(Long userId, String keyword, List<Long> skillIds, Pageable pageable);
    ProjectUserReviewStatSummary queryStatSummaryByUserId(Long userId);
    List<ProjectUserReviewSummary> queryReviewSummaryByUserId(Long userId, Pageable pageable);
    Map<Long, Double> queryAverageReviewScoresByUserIds(List<Long> userIds);
    List<MyProjectSummaryResponse> queryMyProjectSummary(Long userId);
    Long queryProjectCount(Long userId);
    Long queryReviewCount(Long userId);
    List<ProjectIdTitleProjection> queryUserProjectIdTitlePairs(List<Long> projectIds);
    PageResponse<List<ProjectApplicantResponse>> queryProjectApplicants(Long projectId, Pageable pageable);
    List<ProjectRole> queryProjectRoles();
}
