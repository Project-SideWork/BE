package com.sidework.project.application.service;

import com.sidework.project.application.exception.InvalidCommandException;
import com.sidework.project.application.exception.ProjectDeleteAuthorityException;
import com.sidework.project.application.exception.ProjectNotChangeableException;
import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.ProjectCommandUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectCommandService implements ProjectCommandUseCase {
    private final ProjectOutPort projectRepository;
    private final ProjectUserOutPort projectUserRepository;

    @Override
    //TODO: UserDetails 도입 후 하드코딩 제거
    public void create(ProjectCommand command) {
        checkDateRangeIsValid(command.startDt(), command.endDt());
        checkProjectTitleExists(1L, command.title());

        Project project = Project.create(command.title(), command.description(),
                command.startDt(), command.endDt(), command.meetingType());
        Long savedId = projectRepository.save(project);

        ProjectUser projectUser = ProjectUser.create(1L, savedId, ApplyStatus.ACCEPTED, command.myRole());
        ProjectUser ownerUser = ProjectUser.create(1L, savedId, ApplyStatus.ACCEPTED, ProjectRole.OWNER);

        projectUserRepository.save(projectUser);
        projectUserRepository.save(ownerUser);
    }

    @Override
    public void update(Long projectId, ProjectCommand command) {
        projectRepository.existsById(projectId);
        checkDateRangeIsValid(command.startDt(), command.endDt());
        checkProjectTitleExists(1L, command.title());

        Project project = projectRepository.findById(projectId);
        checkProjectIsChangeable(projectId, project.getStatus());
        project.update(
                command.title(), command.description(), command.startDt(),
                command.endDt(), command.meetingType(), command.status()
        );
        projectRepository.save(project);
    }

    @Override
    public void delete(Long userId, Long projectId) {
        List<ProjectRole> myRoles = projectUserRepository.queryUserRolesByProject(userId, projectId);
        checkCanDelete(projectId, myRoles);

        Project project = projectRepository.findById(projectId);
        project.delete();

        projectRepository.save(project);
    }


    private void checkCanDelete(Long projectId, List<ProjectRole> myRoles) {
        if(!myRoles.contains(ProjectRole.OWNER)) {
            throw new ProjectDeleteAuthorityException(projectId);
        }
    }

    private void checkDateRangeIsValid(LocalDate startDt, LocalDate endDt) {
        if(startDt.isAfter(endDt)) {
            throw new InvalidCommandException();
        }
    }

    private void checkProjectIsChangeable(Long projectId, ProjectStatus status){
        if(status.equals(ProjectStatus.CANCELED) || status.equals(ProjectStatus.CLOSED) || status.equals(ProjectStatus.FINISHED)) {
            throw new ProjectNotChangeableException(projectId);
        }
    }

    private void checkProjectTitleExists(Long userId, String title) {
        List<Long> myProjects = projectUserRepository.queryAllProjectIds(userId);
        if (myProjects == null || myProjects.isEmpty()) {
            return;
        }

        List<String> titles = projectRepository. findAllTitles(myProjects);

        if (titles.contains(title)) {
            throw new InvalidCommandException();
        }
    }
}
