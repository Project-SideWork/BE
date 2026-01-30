package com.sidework.project.application.service;

import static com.sidework.project.domain.ApplyStatus.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.exception.ProjectNotRecruitingException;
import com.sidework.project.application.port.in.ProjectApplyCommand;
import com.sidework.project.application.port.in.ProjectApplyCommandUseCase;
import com.sidework.project.application.port.out.ProfileValidationOutPort;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectApplyCommandService implements ProjectApplyCommandUseCase {

	private final ProjectOutPort projectRepository;
	private final ProjectUserOutPort projectUserRepository;
	private final ProfileValidationOutPort profileValidationOutPort;

	@Override
	public void apply(Long userId, Long projectId, ProjectApplyCommand command) {
		Long profileId = command.profileId();
		checkProjectExistsAndIsRecruiting(projectId);
		profileValidationOutPort.validateProfileExistsAndOwnedByUser(profileId, userId);
		projectUserRepository.save(
			ProjectUser.create(
				userId,
				projectId,
				profileId,
				UNREAD,
				command.role()
			)
		);
	}

	private void checkProjectExistsAndIsRecruiting(Long projectId) {
		if (!projectRepository.existsById(projectId)) {
			throw new ProjectNotFoundException(projectId);
		}
		if(!projectRepository.findById(projectId)
			.getStatus().equals(ProjectStatus.RECRUITING))
		{
			throw new ProjectNotRecruitingException(projectId);
		}
	}
}
