package com.sidework.project.application.service;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.application.exception.InvalidCommandException;
import com.sidework.project.application.exception.ProjectDeleteAuthorityException;
import com.sidework.project.application.exception.ProjectNotChangeableException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.ProjectCommandUseCase;
import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectRecruitPositionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.*;
import com.sidework.skill.application.port.in.ProjectPreferredSkillCommandUseCase;
import com.sidework.skill.application.port.in.ProjectRequiredCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectCommandService implements ProjectCommandUseCase {
    private final ProjectPreferredSkillCommandUseCase preferredSkillCommandService;
    private final ProjectRequiredCommandUseCase requiredSkillCommandService;
    private final ProjectOutPort projectRepository;
    private final ProjectUserOutPort projectUserRepository;
    private final ProjectRecruitPositionOutPort projectRecruitPositionRepository;

    @Override
    //TODO: UserDetails 도입 후 하드코딩 제거
    public void create(ProjectCommand command) {
        checkDateRangeIsValid(command.startDt(), command.endDt());
        checkProjectTitleExists(1L, command.title(), null);

        Project project = Project.create(command.title(), command.description(),
                command.startDt(), command.endDt(), command.meetingType());
        Long savedId = projectRepository.save(project);

        ProjectUser projectUser = ProjectUser.create(1L, savedId, null,ApplyStatus.ACCEPTED, command.myRole());
        ProjectUser ownerUser = ProjectUser.create(1L, savedId, null,ApplyStatus.ACCEPTED, ProjectRole.OWNER);

        projectUserRepository.save(projectUser);
        projectUserRepository.save(ownerUser);

        requiredSkillCommandService.create(savedId, command.requiredStacks());
        if(!command.preferredStacks().isEmpty()) {
            preferredSkillCommandService.create(savedId, command.preferredStacks());
        }
        List<ProjectRecruitPosition> recruitPositions = toRecruitPositions(savedId, command.recruitPositions());
        projectRecruitPositionRepository.saveAll(savedId, recruitPositions);
    }

    @Override
    public void update(Long projectId, ProjectCommand command) {
        checkProjectExists(projectId);
        checkDateRangeIsValid(command.startDt(), command.endDt());
        checkProjectTitleExists(1L, command.title(), projectId);

        Project project = projectRepository.findById(projectId);
        checkProjectIsChangeable(projectId, project.getStatus());
        project.update(
                command.title(), command.description(), command.startDt(),
                command.endDt(), command.meetingType(), command.status()
        );
        projectRepository.save(project);

        requiredSkillCommandService.update(projectId, command.requiredStacks());
        if(!command.preferredStacks().isEmpty()) {
            preferredSkillCommandService.update(projectId, command.preferredStacks());
        }
        projectRecruitPositionRepository.deleteByProjectId(projectId);
        List<ProjectRecruitPosition> recruitPositions = toRecruitPositions(projectId, command.recruitPositions());
        projectRecruitPositionRepository.saveAll(projectId, recruitPositions);
    }

    @Override
    public void delete(Long userId, Long projectId) {
        checkProjectExists(projectId);
        List<ProjectRole> myRoles = projectUserRepository.queryUserRolesByProject(userId, projectId);
        checkCanDelete(projectId, myRoles);

        Project project = projectRepository.findById(projectId);
        project.delete();

        projectRepository.save(project);

        projectRecruitPositionRepository.deleteByProjectId(projectId);
    }


    private void checkProjectExists(Long projectId) {
        if(!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }
    }

    private void checkCanDelete(Long projectId, List<ProjectRole> myRoles) {
        if(myRoles == null || !myRoles.contains(ProjectRole.OWNER)) {
            throw new ProjectDeleteAuthorityException(projectId);
        }
    }

    private void checkDateRangeIsValid(LocalDate startDt, LocalDate endDt) {
        if(startDt.isAfter(endDt)) {
            throw new InvalidCommandException("시작 일자는 종료 일자보다 빨라야 합니다.");
        }
    }

    private void checkProjectIsChangeable(Long projectId, ProjectStatus status){
        if(status.equals(ProjectStatus.CANCELED) || status.equals(ProjectStatus.CLOSED) || status.equals(ProjectStatus.FINISHED)) {
            throw new ProjectNotChangeableException(projectId);
        }
    }

    private void checkProjectTitleExists(Long userId, String title, Long projectId) {
        List<Long> myProjects = projectUserRepository.queryAllProjectIds(userId);

        if (myProjects == null || myProjects.isEmpty()) {
            return;
        }

        List<ProjectTitleDto> titles = projectRepository.findAllTitles(myProjects);
        boolean titleExists = titles.stream()
                .filter(dto -> !dto.id().equals(projectId))
                .anyMatch(dto -> dto.title().equals(title));

        if (titleExists) {
            throw new InvalidCommandException("참여 중인 프로젝트 중에 동일한 이름이 있습니다.");
        }
    }

    private List<ProjectRecruitPosition> toRecruitPositions(Long projectId, List<RecruitPosition> recruitPositions) {
        if (recruitPositions == null || recruitPositions.isEmpty()) {
            return List.of();
        }
        return recruitPositions.stream()
            .map(position -> ProjectRecruitPosition.create(projectId, position.role(), position.headCount(), position.level()))
            .toList();
    }
}
