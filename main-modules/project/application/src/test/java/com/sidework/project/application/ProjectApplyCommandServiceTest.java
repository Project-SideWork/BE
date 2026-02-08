package com.sidework.project.application;

import static com.sidework.project.domain.ApplyStatus.UNREAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sidework.project.application.exception.ProfileNotFoundException;
import com.sidework.project.application.exception.ProjectAlreadyAppliedException;
import com.sidework.project.application.exception.ProjectNotRecruitingException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectApplyCommand;
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

	@InjectMocks
	private ProjectApplyCommandService projectApplyCommandService;

	@Captor
	private ArgumentCaptor<ProjectUser> projectUserCaptor;

	@Test
	void 정상_지원_요청시_ProjectUser가_저장된다() {
		Long userId = 1L;
		Long projectId = 1L;
		Long profileId = 1L;
		ProjectApplyCommand command = new ProjectApplyCommand(profileId, ProjectRole.BACKEND);

		Project project = Project.builder()
			.id(projectId)
			.status(ProjectStatus.RECRUITING)
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
			.build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.queryUserRolesByProject(userId, projectId)).thenReturn(List.of(ProjectRole.BACKEND));
		when(profileQueryOutPort.existsByIdAndUserId(profileId, userId)).thenReturn(true);

		assertThrows(
			ProjectAlreadyAppliedException.class,
			() -> projectApplyCommandService.apply(userId, projectId, command)
		);
	}
}
