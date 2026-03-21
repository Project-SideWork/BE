package com.sidework.project.application;

import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.application.port.out.ProjectLikeOutPort;
import com.sidework.project.application.service.ProjectLikeCommandService;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectLike;
import com.sidework.user.application.port.in.UserQueryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectLikeCommandServiceTest {

	@Mock
	private ProjectLikeOutPort projectLikeOutPort;

	@Mock
	private ProjectQueryUseCase projectQueryUseCase;

	@Mock
	private UserQueryUseCase userQueryUseCase;

	@InjectMocks
	private ProjectLikeCommandService service;

	@Captor
	private ArgumentCaptor<ProjectLike> projectLikeCaptor;

	@Test
	void 프로젝트_좋아요에_성공한다() {
		// given
		Long userId = 1L;
		Long projectId = 2L;
		when(projectQueryUseCase.queryById(projectId)).thenReturn(Project.builder().id(projectId).build());
		doNothing().when(userQueryUseCase).validateExists(userId);

		// when
		service.like(userId, projectId);

		// then
		verify(projectQueryUseCase).queryById(projectId);
		verify(userQueryUseCase).validateExists(userId);
		verify(projectLikeOutPort).like(projectLikeCaptor.capture());
		ProjectLike saved = projectLikeCaptor.getValue();
		assertEquals(userId, saved.getUserId());
		assertEquals(projectId, saved.getProjectId());
	}

	@Test
	void 존재하지_않는_projectId로_좋아요_시도_시_ProjectNotFoundException을_던진다() {
		// given
		Long userId = 1L;
		Long projectId = 2L;
		when(projectQueryUseCase.queryById(projectId)).thenThrow(new ProjectNotFoundException(projectId));

		// when & then
		assertThrows(
			ProjectNotFoundException.class,
			() -> service.like(userId, projectId)
		);

		verify(projectQueryUseCase).queryById(projectId);
		verify(userQueryUseCase, never()).validateExists(any());
		verify(projectLikeOutPort, never()).like(any(ProjectLike.class));
	}
}
