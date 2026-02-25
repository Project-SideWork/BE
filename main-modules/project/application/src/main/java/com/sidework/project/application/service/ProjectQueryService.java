package com.sidework.project.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.application.adapter.ProjectListResponse;
import com.sidework.project.application.exception.ProjectHasNoMembersException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.application.adapter.ProjectDetailResponse.RecruitPositionResponse;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectRecruitPositionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectUser;
import com.sidework.skill.application.port.in.ProjectPreferredSkillQueryUseCase;
import com.sidework.skill.application.port.in.ProjectRequiredQueryUseCase;

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

    private final ProjectPreferredSkillQueryUseCase projectPreferredSkillQueryUseCase;
    private final ProjectRequiredQueryUseCase projectRequiredQueryUseCase;



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
    public ProjectDetailResponse queryProjectDetail(Long projectId) {
        Project project = queryById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(projectId);
        }

        List<ProjectUser> allMembers = projectUserRepository.findAllByProjectId(projectId);
        if (allMembers == null || allMembers.isEmpty()) {
            throw new ProjectHasNoMembersException(projectId);
        }

        List<ProjectRecruitPosition> positions = queryProjectRecruitPosition(projectId);

        List<ProjectDetailResponse.ProjectMemberResponse> teamMembers = buildTeamMembers(allMembers);
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
            preferredStacks
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
    public PageResponse<List<ProjectListResponse>> queryProjectList(Pageable pageable) {
        Page<Project> page = projectRepository.findPage(pageable);
        List<Project>  projects = page.getContent();
        return null;

    }

    private List<String> queryRequiredStacks(Long projectId) {
		return Optional.ofNullable(projectRequiredQueryUseCase.queryNamesByProjectId(projectId))
            .orElse(List.of());
    }

    private List<String> queryPreferredSkills(Long projectId) {
        return Optional.ofNullable(projectPreferredSkillQueryUseCase.queryNamesByProjectId(projectId))
            .orElse(List.of());
    }

    private List<ProjectDetailResponse.ProjectMemberResponse> buildTeamMembers(List<ProjectUser> allMembers) {
        return allMembers.stream()
            .map(member -> ProjectDetailResponse.ProjectMemberResponse.of(
                member.getUserId(),
                member.getProfileId(),
                member.getRole(),
                member.getStatus()
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

}
