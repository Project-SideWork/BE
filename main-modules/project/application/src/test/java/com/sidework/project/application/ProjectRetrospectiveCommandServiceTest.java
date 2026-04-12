package com.sidework.project.application;

import com.sidework.project.application.exception.ProjectNotFinishedException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.exception.ProjectRetrospectiveAlreadyExistsException;
import com.sidework.project.application.exception.ProjectUserNotAcceptedException;
import com.sidework.project.application.port.in.ProjectRetrospectiveCommand;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectRetrospectiveOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.service.ProjectRetrospectiveCommandService;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectRetrospectiveCommandServiceTest {

	@Mock
	private ProjectRetrospectiveOutPort projectRetrospectiveOutPort;

	@Mock
	private ProjectOutPort projectOutPort;

	@Mock
	private ProjectUserOutPort projectUserOutPort;

	@InjectMocks
	private ProjectRetrospectiveCommandService service;

	@Test
	void create_성공한다() {
		Long projectId = 10L;
		Long userId = 1L;
		ProjectUser member = ProjectUser.builder().userId(userId).status(ApplyStatus.ACCEPTED).build();
		ProjectRetrospectiveCommand command = new ProjectRetrospectiveCommand("역할", "잘한 점", "아쉬운 점", "배운 점");

		when(projectOutPort.existsById(projectId)).thenReturn(true);
		when(projectOutPort.getProjectStatus(projectId)).thenReturn(ProjectStatus.FINISHED);
		when(projectUserOutPort.findAcceptedByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(member));
		when(projectRetrospectiveOutPort.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

		service.create(userId, projectId, command);

		verify(projectRetrospectiveOutPort).save(any());
	}

	@Test
	void create_프로젝트가_없으면_ProjectNotFoundException을_던진다() {
		Long projectId = 10L;
		when(projectOutPort.existsById(projectId)).thenReturn(false);

		ProjectRetrospectiveCommand command = new ProjectRetrospectiveCommand("역할", "잘한 점", "아쉬운 점", "배운 점");

		assertThrows(ProjectNotFoundException.class, () -> service.create(1L, projectId, command));
	}

	@Test
	void create_프로젝트가_종료되지_않으면_ProjectNotFinishedException을_던진다() {
		Long projectId = 10L;
		when(projectOutPort.existsById(projectId)).thenReturn(true);
		when(projectOutPort.getProjectStatus(projectId)).thenReturn(ProjectStatus.RECRUITING);

		ProjectRetrospectiveCommand command = new ProjectRetrospectiveCommand("역할", "잘한 점", "아쉬운 점", "배운 점");

		assertThrows(ProjectNotFinishedException.class, () -> service.create(1L, projectId, command));
	}

	@Test
	void create_승인된_멤버가_아니면_ProjectUserNotAcceptedException을_던진다() {
		Long projectId = 10L;
		Long userId = 1L;
		when(projectOutPort.existsById(projectId)).thenReturn(true);
		when(projectOutPort.getProjectStatus(projectId)).thenReturn(ProjectStatus.FINISHED);
		when(projectUserOutPort.findAcceptedByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

		ProjectRetrospectiveCommand command = new ProjectRetrospectiveCommand("역할", "잘한 점", "아쉬운 점", "배운 점");

		assertThrows(ProjectUserNotAcceptedException.class, () -> service.create(userId, projectId, command));
	}

	@Test
	void create_이미_작성했으면_ProjectRetrospectiveAlreadyExistsException을_던진다() {
		Long projectId = 10L;
		Long userId = 1L;
		ProjectUser member = ProjectUser.builder().userId(userId).status(ApplyStatus.ACCEPTED).build();

		when(projectOutPort.existsById(projectId)).thenReturn(true);
		when(projectOutPort.getProjectStatus(projectId)).thenReturn(ProjectStatus.FINISHED);
		when(projectUserOutPort.findAcceptedByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(member));
		when(projectRetrospectiveOutPort.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);

		ProjectRetrospectiveCommand command = new ProjectRetrospectiveCommand("역할", "잘한 점", "아쉬운 점", "배운 점");

		assertThrows(ProjectRetrospectiveAlreadyExistsException.class, () -> service.create(userId, projectId, command));
	}
}
