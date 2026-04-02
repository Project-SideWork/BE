package com.sidework.project.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sidework.project.application.dto.ProjectPromotionCommand;
import com.sidework.project.application.exception.InvalidCommandException;
import com.sidework.project.application.exception.ProjectNotFinishedException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.service.ProjectPromotionCommandService;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;
import com.sidework.skill.application.service.ProjectPromotionSkillCommandService;

@ExtendWith(MockitoExtension.class)
class ProjectPromotionCommandServiceTest {

	@Mock
	private ProjectOutPort projectRepository;

	@Mock
	private ProjectPromotionOutPort projectPromotionRepository;

	@Mock
	private ProjectPromotionSkillCommandService promotionSkillCommandService;

	@Mock
	private ProjectUserOutPort projectUserOutPort;

	@InjectMocks
	private ProjectPromotionCommandService service;

	@Test
	void create_프로젝트가_종료되고_수락된_멤버이면_저장하고_스킬을_등록한다() {
		Long userId = 1L;
		Long projectId = 10L;
		ProjectPromotionCommand command = new ProjectPromotionCommand(
			"홍보",
			List.of(1L, 2L),
			"https://demo"
		);

		when(projectRepository.existsById(projectId)).thenReturn(true);
		when(projectRepository.getProjectStatus(projectId)).thenReturn(ProjectStatus.FINISHED);
		when(projectUserOutPort.findAcceptedByProjectIdAndUserId(projectId, userId))
			.thenReturn(Optional.of(ProjectUser.builder().projectId(projectId).userId(userId).build()));
		when(projectPromotionRepository.save(any(ProjectPromotion.class))).thenReturn(100L);
		doNothing().when(promotionSkillCommandService)
			.create(eq(userId), eq(100L), eq(projectId), eq(command.usedSkillIds()));

		service.create(userId, projectId, command);

		verify(projectPromotionRepository).save(any(ProjectPromotion.class));
		verify(promotionSkillCommandService).create(userId, 100L, projectId, command.usedSkillIds());
	}

	@Test
	void create_프로젝트가_없으면_ProjectNotFoundException() {
		Long projectId = 10L;
		when(projectRepository.existsById(projectId)).thenReturn(false);

		assertThrows(
			ProjectNotFoundException.class,
			() -> service.create(1L, projectId, new ProjectPromotionCommand("d", List.of(), null))
		);
		verify(projectPromotionRepository, never()).save(any());
		verify(promotionSkillCommandService, never()).create(any(), any(), any(), any());
	}

	@Test
	void create_프로젝트가_종료가_아니면_ProjectNotFinishedException() {
		Long projectId = 10L;
		when(projectRepository.existsById(projectId)).thenReturn(true);
		when(projectRepository.getProjectStatus(projectId)).thenReturn(ProjectStatus.RECRUITING);

		assertThrows(
			ProjectNotFinishedException.class,
			() -> service.create(1L, projectId, new ProjectPromotionCommand("d", List.of(), null))
		);
		verify(projectPromotionRepository, never()).save(any());
	}

	@Test
	void update_경로_프로젝트와_홍보_프로젝트가_같으면_저장하고_스킬을_갱신한다() {
		Long userId = 1L;
		Long projectId = 10L;
		Long promotionId = 50L;
		ProjectPromotion promotion = ProjectPromotion.builder()
			.id(promotionId)
			.projectId(projectId)
			.userId(userId)
			.description("old")
			.demoUrl(null)
			.build();
		ProjectPromotionCommand command = new ProjectPromotionCommand("new", List.of(3L), "u");

		when(projectPromotionRepository.findByIdAndUserId(promotionId, userId)).thenReturn(promotion);
		when(projectPromotionRepository.save(any(ProjectPromotion.class))).thenReturn(promotionId);
		doNothing().when(promotionSkillCommandService)
			.update(eq(userId), eq(promotionId), eq(projectId), eq(command.usedSkillIds()));

		service.update(userId, promotionId, projectId, command);

		verify(projectPromotionRepository).save(any(ProjectPromotion.class));
		verify(promotionSkillCommandService).update(userId, promotionId, projectId, command.usedSkillIds());
	}

	@Test
	void update_경로_projectId가_홍보의_프로젝트와_다르면_InvalidCommandException() {
		Long userId = 1L;
		Long promotionId = 50L;
		ProjectPromotion promotion = ProjectPromotion.builder()
			.id(promotionId)
			.projectId(99L)
			.userId(userId)
			.description("old")
			.demoUrl(null)
			.build();

		when(projectPromotionRepository.findByIdAndUserId(promotionId, userId)).thenReturn(promotion);

		assertThrows(
			InvalidCommandException.class,
			() -> service.update(userId, promotionId, 10L, new ProjectPromotionCommand("n", List.of(), null))
		);
		verify(projectPromotionRepository, never()).save(any());
		verify(promotionSkillCommandService, never()).update(any(), any(), any(), any());
	}

	@Test
	void delete_경로가_일치하면_홍보를_삭제한다() {
		Long userId = 1L;
		Long projectId = 10L;
		Long promotionId = 50L;
		ProjectPromotion promotion = ProjectPromotion.builder()
			.id(promotionId)
			.projectId(projectId)
			.userId(userId)
			.build();

		when(projectPromotionRepository.findByIdAndUserId(promotionId, userId)).thenReturn(promotion);

		service.delete(userId, promotionId, projectId);

		verify(projectPromotionRepository).deleteById(promotionId);
	}

	@Test
	void delete_경로_projectId가_다르면_InvalidCommandException() {
		Long userId = 1L;
		Long promotionId = 50L;
		ProjectPromotion promotion = ProjectPromotion.builder()
			.id(promotionId)
			.projectId(99L)
			.userId(userId)
			.build();

		when(projectPromotionRepository.findByIdAndUserId(promotionId, userId)).thenReturn(promotion);

		assertThrows(
			InvalidCommandException.class,
			() -> service.delete(userId, promotionId, 10L)
		);
		verify(projectPromotionRepository, never()).deleteById(any());
	}
}
