package com.sidework.project.application.service;

import static com.sidework.project.domain.ApplyStatus.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.event.ProjectApplyDecisionEvent;
import com.sidework.project.application.exception.ProfileNotFoundException;
import com.sidework.project.application.exception.ProjectApplicantNotFoundException;
import com.sidework.project.application.exception.ProjectAlreadyAppliedException;
import com.sidework.project.application.exception.ProjectApplyAlreadyProcessedException;
import com.sidework.project.application.exception.ProjectNotRecruitingException;
import com.sidework.project.application.exception.ProjectOwnerNotFoundException;
import com.sidework.project.application.exception.ProjectUserNotFoundException;
import com.sidework.project.application.port.in.ProjectApplyCommand;
import com.sidework.project.application.port.in.ProjectApplyDecisionCommand;
import com.sidework.project.application.port.in.ProjectApplyCommandUseCase;
import com.sidework.project.application.port.out.ProfileQueryOutPort;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectApplyCommandService implements ProjectApplyCommandUseCase {

	private final ProjectOutPort projectRepository;
	private final ProjectUserOutPort projectUserRepository;
	private final ProfileQueryOutPort profileQueryRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void apply(Long userId, Long projectId, ProjectApplyCommand command) {
		Long profileId = command.profileId();
		checkProjectExistsAndIsRecruiting(projectId);
		validateProfileExistsAndOwnedByUser(profileId, userId);
		checkDuplicateApply(userId, projectId, command.role());
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

	@Override
	public void approve(Long userId, Long projectId, ProjectApplyDecisionCommand command) {
		Project project = checkProjectExistsAndIsRecruiting(projectId);
		validateOwnerOrThrow(userId, projectId);
		ProjectUser applicant = validateApplicantPendingOrThrow(command.applicantUserId(), projectId, command.role());
		applicant.updateStatus(ACCEPTED);

		projectUserRepository.save(applicant);

		eventPublisher.publishEvent(
			new ProjectApplyDecisionEvent(projectId, command.applicantUserId(), project.getTitle(), applicant.getRole().getValue(), true)
		);
	}

	@Override
	public void reject(Long userId, Long projectId, ProjectApplyDecisionCommand command) {
		Project project = checkProjectExistsAndIsRecruiting(projectId);
		validateOwnerOrThrow(userId, projectId);
		ProjectUser applicant = validateApplicantPendingOrThrow(command.applicantUserId(), projectId, command.role());
		applicant.updateStatus(REJECTED);

		projectUserRepository.save(applicant);

		eventPublisher.publishEvent(
			new ProjectApplyDecisionEvent(projectId, command.applicantUserId(), project.getTitle(), applicant.getRole().getValue(), false)
		);
	}

	private void validateProfileExistsAndOwnedByUser(Long profileId, Long userId) {
		if (!profileQueryRepository.existsByIdAndUserId(profileId, userId)) {
			throw new ProfileNotFoundException(profileId);
		}
	}

	private Project checkProjectExistsAndIsRecruiting(Long projectId) {
		Project project = projectRepository.findById(projectId);
		if (!project.getStatus().equals(ProjectStatus.RECRUITING)) {
			throw new ProjectNotRecruitingException(projectId);
		}
		return project;
	}

	private void checkDuplicateApply(Long userId, Long projectId, ProjectRole role) {
		if (projectUserRepository.queryUserRolesByProject(userId, projectId).contains(role)) {
			throw new ProjectAlreadyAppliedException(projectId);
		}
	}

	private void validateOwnerOrThrow(Long userId, Long projectId) {
		List<ProjectRole> roles = projectUserRepository.queryUserRolesByProject(userId, projectId);

		if (roles.isEmpty()) {
			throw new ProjectUserNotFoundException(projectId);
		}

		boolean isOwner = roles.contains(ProjectRole.OWNER);

		if (!isOwner) {
			throw new ProjectOwnerNotFoundException(projectId);
		}
	}

	private ProjectUser validateApplicantPendingOrThrow(Long applicantUserId, Long projectId, ProjectRole applicantRole) {
		ProjectUser projectUser = projectUserRepository.findByProjectIdAndUserIdAndRole(projectId, applicantUserId, applicantRole)
			.orElseThrow(() -> new ProjectApplicantNotFoundException(projectId));

		if (projectUser.getStatus() != UNREAD && projectUser.getStatus() != READ) {
			throw new ProjectApplyAlreadyProcessedException(projectId, applicantUserId);
		}
		return projectUser;
	}

}
