package com.sidework.project.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.MyProjectSummaryResponse;
import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.application.adapter.ProjectListResponse;
import com.sidework.project.application.dto.ProjectUserReviewStatSummary;
import com.sidework.project.application.dto.ProjectUserReviewSummary;
import com.sidework.project.application.exception.ProjectHasNoMembersException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectLikeQueryUseCase;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.application.adapter.ProjectDetailResponse.RecruitPositionResponse;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectRecruitPositionOutPort;
import com.sidework.project.application.port.out.ProjectRetrospectiveOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewStatOutPort;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.domain.ProjectRetrospective;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectUser;
import com.sidework.project.domain.ProjectUserReview;
import com.sidework.project.domain.ProjectUserReviewStat;
import com.sidework.skill.application.port.in.ProjectPreferredSkillQueryUseCase;
import com.sidework.skill.application.port.in.ProjectRequiredQueryUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryService implements ProjectQueryUseCase {
    private final ProjectOutPort projectRepository;
    private final ProjectUserOutPort projectUserRepository;
    private final ProjectRecruitPositionOutPort projectRecruitPositionRepository;
    private final ProjectUserReviewStatOutPort projectUserReviewStatRepository;
    private final ProjectUserReviewOutPort projectUserReviewOutPort;
    private final ProjectRetrospectiveOutPort projectRetrospectiveOutPort;

    private final ProjectPreferredSkillQueryUseCase projectPreferredSkillQueryUseCase;
    private final ProjectRequiredQueryUseCase projectRequiredQueryUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final ProjectLikeQueryUseCase projectLikeQueryUseCase;


    @Override
    public Project queryById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public List<Project> queryByUserId(Long userId) {
        List<Long> projectIds = projectUserRepository.queryAllProjectIds(userId);
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }
        return projectRepository.findByIdIn(projectIds);
    }

    @Override
    public ProjectDetailResponse queryProjectDetail(Long userId, Long projectId) {
        Project project = queryById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(projectId);
        }

        ProjectRetrospective rowRetrospective = projectRetrospectiveOutPort.findByProjectIdAndUserId(projectId, userId);

        List<ProjectUser> allMembers = deduplicateMembersByUserId(projectId);

        List<Long> userIds = allMembers.stream()
            .map(ProjectUser::getUserId)
            .toList();

        List<ProjectRecruitPosition> positions = queryProjectRecruitPosition(projectId);
        Map<Long, Double> avgScoreByUserId = buildScoreByUserId(userIds);

        List<ProjectDetailResponse.ProjectMemberResponse> teamMembers = buildTeamMembers(allMembers,avgScoreByUserId);
        List<RecruitPositionResponse> recruitPositions = buildRecruitPositions(positions);
        List<String> requiredStacks = queryRequiredStacks(projectId);
        List<String> preferredStacks = queryPreferredSkills(projectId);

        return new ProjectDetailResponse(
            project.getId(),
            project.getTitle(),
            project.getDescription(),
            project.getStartDt(),
            project.getEndDt(),
            project.getMeetingType(),
            project.getStatus(),
            teamMembers,
            recruitPositions,
            requiredStacks,
            preferredStacks,
            buildRetrospectiveResponse(rowRetrospective)
        );
    }

    @Override
    public List<ProjectRecruitPosition> queryProjectRecruitPosition(Long projectId) {
        List<ProjectRecruitPosition> positions = projectRecruitPositionRepository.getProjectRecruitPositions(projectId);
        if (positions == null || positions.isEmpty()) {
            return List.of();
        }
        return positions;
    }

    @Override
    public Map<Long, List<ProjectRole>> queryUserRolesByProjects(Long userId, List<Long> projectIds) {
        return projectUserRepository.queryUserRolesByProjects(userId, projectIds);
    }

    @Override
    public PageResponse<List<ProjectListResponse>> queryProjectList(Long userId, Pageable pageable) {
        Page<Project> page = projectRepository.findPage(pageable);
        return buildProjectListPageResponse(userId, page);
    }

    @Override
    public PageResponse<List<ProjectListResponse>> queryProjectList(Long userId, String keyword, List<Long> skillIds, Pageable pageable) {
        Page<Project> page = projectRepository.search(keyword, skillIds, pageable);
        return buildProjectListPageResponse(userId, page);
    }

	@Override
	public PageResponse<List<ProjectListResponse>> queryLikedProjectList(Long userId, String keyword, List<Long> skillIds, Pageable pageable) {
		Page<Project> page = projectRepository.searchLiked(keyword, skillIds, userId, pageable);
		return buildProjectListPageResponse(userId, page);
	}

    @Override
    public ProjectUserReviewStatSummary queryStatSummaryByUserId(Long userId) {
        ProjectUserReviewStat stat = projectUserReviewStatRepository.getReviewStatByUserId(userId);
        if (stat == null) {
            return null;
        }
        return ProjectUserReviewStatSummary.of(stat);
    }

    @Override
    public List<ProjectUserReviewSummary> queryReviewSummaryByProjectIds(Long userId, List<Long> projectIds) {
        List<ProjectUserReview> reviews = projectUserReviewOutPort.getReviewsByUserIdAndProjectIds(userId,projectIds);
        if(reviews == null || reviews.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = reviews.stream()
            .map(ProjectUserReview::getReviewerUserId)
            .distinct()
            .toList();

        Map<Long, String> userNames = userQueryUseCase.findNamesByUserIds(userIds);

        return reviews.stream()
            .map(review -> {
                double score = calculateScore(review);
                String reviewerName =
                    userNames.getOrDefault(review.getReviewerUserId(), "unknown");

                return ProjectUserReviewSummary.of(
                    review,
                    reviewerName,
                    score
                );
            })
            .toList();
    }

    @Override
    public List<MyProjectSummaryResponse> queryMyProjectSummary(Long userId) {
        return projectUserRepository.getMyProjectSummary(userId).stream()
            .map(dto -> MyProjectSummaryResponse.create(dto.id(), dto.title()))
            .toList();
    }

    private PageResponse<List<ProjectListResponse>> buildProjectListPageResponse(Long userId, Page<Project> page) {
        List<Project> projects = page.getContent();
        if (projects.isEmpty()) {
            return PageResponse.from(page, List.of());
        }
        List<Long> projectIds = projects.stream()
            .map(Project::getId)
            .toList();

        ListBatchData batch = loadListBatchData(userId, projectIds);
        List<ProjectListResponse> contents = buildListResponses(projects, batch);

        return PageResponse.from(page, contents);
    }

    private ListBatchData loadListBatchData(Long userId, List<Long> projectIds) {
        Map<Long, List<ProjectRecruitPosition>> positionsMap = projectRepository.getProjectRecruitPositionsByProjectIds(projectIds);

        Map<Long, List<String>> requiredStacksMap = projectRequiredQueryUseCase.queryNamesByProjectIds(projectIds);

        Map<Long, Long> ownerUserIdByProject = projectUserRepository.findOwnerUserIdByProjectIds(projectIds);

        Map<Long, Boolean> isLikedProject = projectLikeQueryUseCase.isLikedByProjectIds(userId, projectIds);

		List<Long> ownerUserIds = ownerUserIdByProject.values()
			.stream()
			.distinct()
			.toList();

        Map<Long, String> userIdToName = userQueryUseCase.findNamesByUserIds(ownerUserIds);
        return new ListBatchData(
			positionsMap,
			requiredStacksMap,
			ownerUserIdByProject,
			userIdToName,
			isLikedProject);
    }

    private List<ProjectListResponse> buildListResponses(List<Project> projects, ListBatchData batch) {
        return projects.stream()
            .map(project -> toProjectListResponse(
                project,
                batch.positionsMap.getOrDefault(project.getId(), List.of()),
                batch.requiredStacksMap.getOrDefault(project.getId(), List.of()),
                resolveCreatorName(project.getId(), batch),
                batch.likedByProjectId.getOrDefault(project.getId(), false)))
            .toList();
    }

    private String resolveCreatorName(Long projectId, ListBatchData batch) {
        Long ownerUserId = batch.ownerUserIdByProject.get(projectId);
        return ownerUserId != null ? batch.userIdToName.get(ownerUserId) : null;
    }

    private record ListBatchData(
        Map<Long, List<ProjectRecruitPosition>> positionsMap,
        Map<Long, List<String>> requiredStacksMap,
		Map<Long, Long> ownerUserIdByProject,
        Map<Long, String> userIdToName,
        Map<Long, Boolean> likedByProjectId
    ) {}

        private ProjectListResponse toProjectListResponse(Project project, List<ProjectRecruitPosition> positions, List<String> requiredStacks, String creatorName, boolean isLikedProject) {

        List<RecruitPositionResponse> recruitPositions = buildRecruitPositions(positions);
        return ProjectListResponse.of(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getStatus(),
                isLikedProject,
                recruitPositions,
                requiredStacks != null ? requiredStacks : List.of(),
                creatorName != null ? creatorName : "");
    }

    private List<String> queryRequiredStacks(Long projectId) {
		return Optional.ofNullable(projectRequiredQueryUseCase.queryNamesByProjectId(projectId))
            .orElse(List.of());
    }

    private List<String> queryPreferredSkills(Long projectId) {
        return Optional.ofNullable(projectPreferredSkillQueryUseCase.queryNamesByProjectId(projectId))
            .orElse(List.of());
    }

    private List<ProjectDetailResponse.ProjectMemberResponse> buildTeamMembers(List<ProjectUser> allMembers, Map<Long, Double> avgScoreByUserId) {
        return allMembers.stream()
            .map(member -> ProjectDetailResponse.ProjectMemberResponse.of(
                member.getUserId(),
                member.getProfileId(),
                member.getRole(),
                member.getStatus(),
                avgScoreByUserId.get(member.getUserId())
            ))
            .toList();
    }

    private List<RecruitPositionResponse> buildRecruitPositions(List<ProjectRecruitPosition> positions) {
        return positions.stream()
            .map(pos -> RecruitPositionResponse.of(
                pos.getRole(),
                pos.getHeadCount(),
                pos.getCurrentCount(),
                pos.getLevel()
            ))
            .toList();
    }

    private Map<Long, Double> buildScoreByUserId(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        List<ProjectUserReviewStat> stats = projectUserReviewStatRepository.getAllReviewStatsByUserIds(userIds);

        Map<Long, Double> statMap = stats.stream()
            .collect(Collectors.toMap(
                ProjectUserReviewStat::getUserId,
                this::calculateReviewScore
            ));

        Map<Long, Double> result = new HashMap<>();

        for (Long userId : userIds) {
            result.put(userId, statMap.get(userId));
        }

        return result;
    }


    private double calculateReviewScore(ProjectUserReviewStat stat) {
        return stat.getRatingScore() / stat.getRatingCount();
    }

    private List<ProjectUser> deduplicateMembersByUserId(Long projectId)
    {
        List<ProjectUser> rawMembers = projectUserRepository.findAllByProjectId(projectId);
        if (rawMembers == null || rawMembers.isEmpty()) {
            throw new ProjectHasNoMembersException(projectId);
        }

        List<ProjectUser> allMembers = rawMembers.stream()
            .collect(Collectors.toMap(
                ProjectUser::getUserId,
                Function.identity(),
                (a, b) -> pickOwnerFirst(a, b)
            ))
            .values()
            .stream()
            .toList();

        return allMembers;
    }

    private ProjectUser pickOwnerFirst(ProjectUser a, ProjectUser b) {
        if (a.getRole() == ProjectRole.OWNER) return a;
        if (b.getRole() == ProjectRole.OWNER) return b;
        return a;
    }

    private double calculateScore(ProjectUserReview review) {
        return (
            review.getResponsibility()
                + review.getCommunication()
                + review.getCollaboration()
                + review.getProblemSolving()
        ) / 4.0;
    }

    @Override
    public Map<Long, Double> queryAverageReviewScoresByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return projectUserReviewStatRepository.getAllReviewStatsByUserIds(userIds).stream()
            .collect(Collectors.toMap(
                ProjectUserReviewStat::getUserId,
                stat -> stat.getRatingScore() / stat.getRatingCount()
            ));
    }

    private ProjectDetailResponse.ProjectRetrospectiveResponse buildRetrospectiveResponse(ProjectRetrospective retrospective) {
        if (retrospective == null) {
            return null;
        }
        return ProjectDetailResponse.ProjectRetrospectiveResponse.of(retrospective.getRoleDescription(), retrospective.getStrengths(), retrospective.getImprovements());

    }
}
