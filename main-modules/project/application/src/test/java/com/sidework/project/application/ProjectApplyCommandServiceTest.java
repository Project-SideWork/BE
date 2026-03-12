package com.sidework.project.application;

import static com.sidework.project.domain.ApplyStatus.ACCEPTED;
import static com.sidework.project.domain.ApplyStatus.READ;
import static com.sidework.project.domain.ApplyStatus.REJECTED;
import static com.sidework.project.domain.ApplyStatus.UNREAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.sidework.project.application.event.ProjectApplyDecisionEvent;
import com.sidework.project.application.exception.ProfileNotFoundException;
import com.sidework.project.application.exception.ProjectAlreadyAppliedException;
import com.sidework.project.application.exception.ProjectApplicantNotFoundException;
import com.sidework.project.application.exception.ProjectApplyAlreadyProcessedException;
import com.sidework.project.application.exception.ProjectNotRecruitingException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.exception.ProjectOwnerNotFoundException;
import com.sidework.project.application.port.in.ProjectApplyCommand;
import com.sidework.project.application.port.in.ProjectApplyDecisionCommand;
import com.sidework.project.application.port.out.ProfileQueryOutPort;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.service.ProjectApplyCommandService;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;

@ExtendWith(MockitoExtension.class)
class ProjectApplyCommandServiceTest {

	@Mock
	private ProjectOutPort projectOutPort;

	@Mock
	private ProjectUserOutPort projectUserOutPort;

	@Mock
	private ProfileQueryOutPort profileQueryOutPort;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private ProjectApplyCommandService projectApplyCommandService;

	@Captor
	private ArgumentCaptor<ProjectUser> projectUserCaptor;

	@Captor
	private ArgumentCaptor<ProjectApplyDecisionEvent> eventCaptor;

	@Test
	void 정상_지원_요청시_ProjectUser가_저장된다() {
		Long userId = 1L;
		Long projectId = 1L;
		Long profileId = 1L;
		ProjectApplyCommand command = new ProjectApplyCommand(profileId, ProjectRole.BACKEND);

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(userId, projectId)).thenReturn(Collections.emptyList());
		when(profileQueryOutPort.existsByIdAndUserId(profileId, userId)).thenReturn(true);
		doNothing().when(projectUserOutPort).save(any(ProjectUser.class));

		projectApplyCommandService.apply(userId, projectId, command);

		verify(projectUserOutPort).save(projectUserCaptor.capture());
		ProjectUser saved = projectUserCaptor.getValue();

		assertEquals(userId, saved.getUserId());
		assertEquals(projectId, saved.getProjectId());
		assertEquals(profileId, saved.getProfileId());
		assertEquals(UNREAD, saved.getStatus());
		assertEquals(ProjectRole.BACKEND, saved.getRole());
	}

	@Test
	void 존재하지_않는_프로젝트에_지원시_ProjectNotFoundException을_던진다() {
		Long userId = 1L;
		Long projectId = 999L;
		ProjectApplyCommand command = new ProjectApplyCommand(1L, ProjectRole.BACKEND);

		when(projectOutPort.findById(projectId)).thenThrow(new ProjectNotFoundException(projectId));

		assertThrows(
			ProjectNotFoundException.class,
			() -> projectApplyCommandService.apply(userId, projectId, command)
		);
	}

	@Test
	void 모집중이_아닌_프로젝트에_지원시_ProjectNotRecruitingException을_던진다() {
		Long userId = 1L;
		Long projectId = 1L;
		ProjectApplyCommand command = new ProjectApplyCommand(1L, ProjectRole.BACKEND);

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.CLOSED)
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);

		assertThrows(
			ProjectNotRecruitingException.class,
			() -> projectApplyCommandService.apply(userId, projectId, command)
		);
	}

	@Test
	void 프로필_검증_실패시_예외가_전파된다() {
		Long userId = 1L;
		Long projectId = 1L;
		Long profileId = 999L;
		ProjectApplyCommand command = new ProjectApplyCommand(profileId, ProjectRole.BACKEND);

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(profileQueryOutPort.existsByIdAndUserId(profileId, userId)).thenReturn(false);

		assertThrows(
			ProfileNotFoundException.class,
			() -> projectApplyCommandService.apply(userId, projectId, command)
		);
	}

	@Test
	void 이미_지원한_프로젝트에_같은_role로_재지원시_ProjectAlreadyAppliedException을_던진다() {
		Long userId = 1L;
		Long projectId = 1L;
		Long profileId = 1L;
		ProjectApplyCommand command = new ProjectApplyCommand(profileId, ProjectRole.BACKEND);

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(userId, projectId)).thenReturn(List.of(ProjectRole.BACKEND));
		when(profileQueryOutPort.existsByIdAndUserId(profileId, userId)).thenReturn(true);

		assertThrows(
			ProjectAlreadyAppliedException.class,
			() -> projectApplyCommandService.apply(userId, projectId, command)
		);
	}

	@Test
	void 승인_요청_시_상태가_ACCEPTED로_저장되고_이벤트가_발행된다() {
		Long ownerUserId = 1L;
		Long projectId = 1L;
		Long applicantUserId = 2L;
		String projectTitle = "협업 플랫폼 개발";

		Project project = Project.builder()
			.id(projectId)
			.title(projectTitle)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();
		ProjectUser applicant = ProjectUser.builder()
			.id(10L)
			.projectId(projectId)
			.userId(applicantUserId)
			.profileId(1L)
			.status(UNREAD)
			.role(ProjectRole.BACKEND)
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(ownerUserId, projectId)).thenReturn(List.of(ProjectRole.OWNER));
		when(projectUserOutPort.findByProjectIdAndUserIdAndRole(projectId, applicantUserId, ProjectRole.BACKEND)).thenReturn(Optional.of(applicant));
		doNothing().when(projectUserOutPort).save(any(ProjectUser.class));
		doNothing().when(eventPublisher).publishEvent(any(ProjectApplyDecisionEvent.class));

		projectApplyCommandService.approve(ownerUserId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.BACKEND));

		verify(projectUserOutPort).save(projectUserCaptor.capture());
		ProjectUser saved = projectUserCaptor.getValue();
		assertEquals(ACCEPTED, saved.getStatus());

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		ProjectApplyDecisionEvent event = eventCaptor.getValue();
		assertEquals(projectId, event.projectId());
		assertEquals(applicantUserId, event.applicantUserId());
		assertEquals(projectTitle, event.projectTitle());
		assertEquals(ProjectRole.BACKEND.getValue(), event.projectRole());
		assertTrue(event.approved());
	}

	@Test
	void 거절_요청_시_상태가_REJECTED로_저장되고_이벤트가_발행된다() {
		Long ownerUserId = 1L;
		Long projectId = 1L;
		Long applicantUserId = 2L;
		String projectTitle = "협업 플랫폼 개발";

		Project project = Project.builder()
			.id(projectId)
			.title(projectTitle)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();
		ProjectUser applicant = ProjectUser.builder()
			.id(10L)
			.projectId(projectId)
			.userId(applicantUserId)
			.profileId(1L)
			.status(READ)
			.role(ProjectRole.FRONTEND)
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(ownerUserId, projectId)).thenReturn(List.of(ProjectRole.OWNER));
		when(projectUserOutPort.findByProjectIdAndUserIdAndRole(projectId, applicantUserId, ProjectRole.FRONTEND)).thenReturn(Optional.of(applicant));
		doNothing().when(projectUserOutPort).save(any(ProjectUser.class));
		doNothing().when(eventPublisher).publishEvent(any(ProjectApplyDecisionEvent.class));

		projectApplyCommandService.reject(ownerUserId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.FRONTEND));

		verify(projectUserOutPort).save(projectUserCaptor.capture());
		ProjectUser saved = projectUserCaptor.getValue();
		assertEquals(REJECTED, saved.getStatus());

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		ProjectApplyDecisionEvent event = eventCaptor.getValue();
		assertFalse(event.approved());
	}

	@Test
	void 승인_시_오너가_아니면_ProjectOwnerNotFoundException을_던진다() {
		Long userId = 2L;
		Long projectId = 1L;
		Long applicantUserId = 3L;

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(userId, projectId)).thenReturn(List.of(ProjectRole.BACKEND));

		assertThrows(
			ProjectOwnerNotFoundException.class,
			() -> projectApplyCommandService.approve(userId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.BACKEND))
		);
	}

	@Test
	void 승인_시_지원자가_없으면_ProjectApplicantNotFoundException을_던진다() {
		Long ownerUserId = 1L;
		Long projectId = 1L;
		Long applicantUserId = 999L;

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(ownerUserId, projectId)).thenReturn(List.of(ProjectRole.OWNER));
		when(projectUserOutPort.findByProjectIdAndUserIdAndRole(projectId, applicantUserId, ProjectRole.BACKEND)).thenReturn(Optional.empty());

		assertThrows(
			ProjectApplicantNotFoundException.class,
			() -> projectApplyCommandService.approve(ownerUserId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.BACKEND))
		);
	}

	@Test
	void 승인_시_이미_처리된_지원이면_ProjectApplyAlreadyProcessedException을_던진다() {
		Long ownerUserId = 1L;
		Long projectId = 1L;
		Long applicantUserId = 2L;

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();
		ProjectUser applicant = ProjectUser.builder()
			.id(10L)
			.projectId(projectId)
			.userId(applicantUserId)
			.status(ACCEPTED)
			.role(ProjectRole.BACKEND)
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(ownerUserId, projectId)).thenReturn(List.of(ProjectRole.OWNER));
		when(projectUserOutPort.findByProjectIdAndUserIdAndRole(projectId, applicantUserId, ProjectRole.BACKEND)).thenReturn(Optional.of(applicant));

		assertThrows(
			ProjectApplyAlreadyProcessedException.class,
			() -> projectApplyCommandService.approve(ownerUserId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.BACKEND))
		);
	}

	@Test
	void 승인_시_프로젝트가_모집중이_아니면_ProjectNotRecruitingException을_던진다() {
		Long ownerUserId = 1L;
		Long projectId = 1L;
		Long applicantUserId = 2L;

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.CLOSED)
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);

		assertThrows(
			ProjectNotRecruitingException.class,
			() -> projectApplyCommandService.approve(ownerUserId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.BACKEND))
		);
	}

	@Test
	void 거절_시_오너가_아니면_ProjectOwnerNotFoundException을_던진다() {
		Long userId = 2L;
		Long projectId = 1L;
		Long applicantUserId = 3L;

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(userId, projectId)).thenReturn(List.of(ProjectRole.FRONTEND));

		assertThrows(
			ProjectOwnerNotFoundException.class,
			() -> projectApplyCommandService.reject(userId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.FRONTEND))
		);
	}

	@Test
	void 거절_시_이미_처리된_지원이면_ProjectApplyAlreadyProcessedException을_던진다() {
		Long ownerUserId = 1L;
		Long projectId = 1L;
		Long applicantUserId = 2L;

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
			.endDt(LocalDate.now().plusDays(7))
			.build();
		ProjectUser applicant = ProjectUser.builder()
			.id(10L)
			.projectId(projectId)
			.userId(applicantUserId)
			.status(REJECTED)
			.role(ProjectRole.BACKEND)
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(ownerUserId, projectId)).thenReturn(List.of(ProjectRole.OWNER));
		when(projectUserOutPort.findByProjectIdAndUserIdAndRole(projectId, applicantUserId, ProjectRole.BACKEND)).thenReturn(Optional.of(applicant));

		assertThrows(
			ProjectApplyAlreadyProcessedException.class,
			() -> projectApplyCommandService.reject(ownerUserId, projectId, new ProjectApplyDecisionCommand(applicantUserId, ProjectRole.BACKEND))
		);
	}
}
