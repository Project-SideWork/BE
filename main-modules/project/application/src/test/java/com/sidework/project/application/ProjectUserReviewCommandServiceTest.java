package com.sidework.project.application;

import com.sidework.project.application.dto.ProjectUserReviewCommand;
import com.sidework.project.application.exception.ProjectNotFinishedException;
import com.sidework.project.application.exception.ProjectSelfReviewNotAllowedException;
import com.sidework.project.application.exception.ProjectUserAlreadyReviewedException;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewStatOutPort;
import com.sidework.project.application.service.ProjectUserReviewCommandService;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectUserReviewCommandServiceTest {

	@Mock
	private ProjectOutPort projectOutPort;

	@Mock
	private ProjectUserOutPort projectUserOutPort;

	@Mock
	private ProjectUserReviewOutPort projectUserReviewOutPort;

	@Mock
	private ProjectUserReviewStatOutPort projectUserReviewStatOutPort;

	@InjectMocks
	private ProjectUserReviewCommandService service;

	@Test
	void create_성공한다() {
		Long projectId = 10L;
		Long reviewerUserId = 1L;
		Long revieweeUserId = 2L;
		Project project = Project.builder().id(projectId).status(ProjectStatus.FINISHED).build();
		ProjectUser reviewer = ProjectUser.builder().userId(reviewerUserId).status(ApplyStatus.ACCEPTED).build();
		ProjectUser reviewee = ProjectUser.builder().userId(revieweeUserId).status(ApplyStatus.ACCEPTED).build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.findByProjectIdAndUserId(projectId, reviewerUserId)).thenReturn(Optional.of(reviewer));
		when(projectUserOutPort.findAllByProjectId(projectId)).thenReturn(List.of(reviewer, reviewee));

		ProjectUserReviewCommand command = new ProjectUserReviewCommand(
			List.of(new ProjectUserReviewCommand.Review(
				revieweeUserId,
				5, 5, 4, 5,
				"좋았습니다"
			))
		);

		service.create(reviewerUserId, projectId, command);

		verify(projectUserReviewOutPort).saveAll(anyList());
		verify(projectUserReviewStatOutPort).addAllReviewStats(argThat(list ->
			list.size() == 1 && list.get(0).getUserId().equals(revieweeUserId)));
	}

	@Test
	void create_프로젝트가_종료되지_않으면_ProjectNotFinishedException을_던진다() {
		Long projectId = 10L;
		when(projectOutPort.findById(projectId))
			.thenReturn(Project.builder().id(projectId).status(ProjectStatus.RECRUITING).build());

		ProjectUserReviewCommand command = new ProjectUserReviewCommand(
			List.of(new ProjectUserReviewCommand.Review(2L, 5, 5, 5, 5, null))
		);

		assertThrows(ProjectNotFinishedException.class, () -> service.create(1L, projectId, command));
	}

	@Test
	void create_자기_자신을_평가하면_ProjectSelfReviewNotAllowedException을_던진다() {
		Long projectId = 10L;
		Long userId = 1L;
		Project project = Project.builder().id(projectId).status(ProjectStatus.FINISHED).build();
		ProjectUser member = ProjectUser.builder().userId(userId).status(ApplyStatus.ACCEPTED).build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(member));
		when(projectUserOutPort.findAllByProjectId(projectId)).thenReturn(List.of(member));

		ProjectUserReviewCommand command = new ProjectUserReviewCommand(
			List.of(new ProjectUserReviewCommand.Review(userId, 5, 5, 5, 5, null))
		);

		assertThrows(ProjectSelfReviewNotAllowedException.class, () -> service.create(userId, projectId, command));
	}

	@Test
	void create_중복_평가면_ProjectUserAlreadyReviewedException을_던진다() {
		Long projectId = 10L;
		Long reviewerUserId = 1L;
		Long revieweeUserId = 2L;
		Project project = Project.builder().id(projectId).status(ProjectStatus.FINISHED).build();
		ProjectUser reviewer = ProjectUser.builder().userId(reviewerUserId).status(ApplyStatus.ACCEPTED).build();
		ProjectUser reviewee = ProjectUser.builder().userId(revieweeUserId).status(ApplyStatus.ACCEPTED).build();

		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectUserOutPort.findByProjectIdAndUserId(projectId, reviewerUserId)).thenReturn(Optional.of(reviewer));
		when(projectUserOutPort.findAllByProjectId(projectId)).thenReturn(List.of(reviewer, reviewee));
		doThrow(new DataIntegrityViolationException("duplicate"))
			.when(projectUserReviewOutPort).saveAll(anyList());

		ProjectUserReviewCommand command = new ProjectUserReviewCommand(
			List.of(new ProjectUserReviewCommand.Review(revieweeUserId, 5, 5, 5, 5, null))
		);

		assertThrows(ProjectUserAlreadyReviewedException.class, () -> service.create(reviewerUserId, projectId, command));
	}
}
