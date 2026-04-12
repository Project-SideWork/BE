package com.sidework.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.exception.ProjectNotFinishedException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.exception.ProjectRetrospectiveAlreadyExistsException;
import com.sidework.project.application.exception.ProjectUserNotAcceptedException;
import com.sidework.project.application.port.in.ProjectRetrospectiveCommand;
import com.sidework.project.application.port.in.ProjectRetrospectiveCommandUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectRetrospectiveOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.ProjectRetrospective;
import com.sidework.project.domain.ProjectStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectRetrospectiveCommandService implements ProjectRetrospectiveCommandUseCase {

	private final ProjectRetrospectiveOutPort projectRetrospectiveRepository;
	private final ProjectOutPort projectOutPort;
	private final ProjectUserOutPort projectUserOutPort;

	@Override
	public void create(Long userId, Long projectId, ProjectRetrospectiveCommand command) {
		checkProjectExists(projectId);
		checkProjectFinished(projectId);
		checkAcceptedMember(projectId, userId);
		checkNotAlreadyWritten(projectId, userId);

		ProjectRetrospective retrospective = ProjectRetrospective.create(
			projectId,
			userId,
			command.roleDescription(),
			command.strengths(),
			command.regrets(),
			command.learnings()
		);

		projectRetrospectiveRepository.save(retrospective);
	}

	private void checkProjectExists(Long projectId) {
		if (!projectOutPort.existsById(projectId)) {
			throw new ProjectNotFoundException(projectId);
		}
	}

	private void checkProjectFinished(Long projectId) {
		ProjectStatus status = projectOutPort.getProjectStatus(projectId);
		if (!ProjectStatus.FINISHED.equals(status)) {
			throw new ProjectNotFinishedException(projectId);
		}
	}

	private void checkAcceptedMember(Long projectId, Long userId) {
		if (projectUserOutPort.findAcceptedByProjectIdAndUserId(projectId, userId).isEmpty()) {
			throw new ProjectUserNotAcceptedException(projectId);
		}
	}

	private void checkNotAlreadyWritten(Long projectId, Long userId) {
		if (projectRetrospectiveRepository.existsByProjectIdAndUserId(projectId, userId)) {
			throw new ProjectRetrospectiveAlreadyExistsException();
		}
	}
}
