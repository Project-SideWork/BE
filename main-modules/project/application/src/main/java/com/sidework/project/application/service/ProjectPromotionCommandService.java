package com.sidework.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.dto.ProjectPromotionCommand;
import com.sidework.project.application.exception.ProjectNotFinishedException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.exception.InvalidCommandException;
import com.sidework.project.application.exception.ProjectUserNotFoundException;
import com.sidework.project.application.port.in.ProjectPromotionCommandUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.skill.application.service.ProjectPromotionSkillCommandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectPromotionCommandService implements ProjectPromotionCommandUseCase {
	private final ProjectOutPort projectRepository;
	private final ProjectPromotionOutPort projectPromotionRepository;
	private final ProjectUserOutPort projectUserRepository;

	private final ProjectPromotionSkillCommandService promotionSkillCommandService;


	@Override
	public void create(Long userId, Long projectId, ProjectPromotionCommand command) {
		checkProjectExists(projectId);
		checkCanCreateProjectPromotion(projectId, userId);
		ProjectPromotion promotion = ProjectPromotion.create(projectId, userId, command.description(), command.demoUrl());
		Long promotionId = projectPromotionRepository.save(promotion);
		promotionSkillCommandService.create(userId, promotionId, projectId, command.usedSkillIds());

	}

	@Override
	public void update(Long userId, Long promotionId, Long projectId, ProjectPromotionCommand command) {
		ProjectPromotion promotion = checkPromotionExists(promotionId, userId);
		if (!projectId.equals(promotion.getProjectId())) {
			throw new InvalidCommandException("요청 경로의 프로젝트와 홍보글이 속한 프로젝트가 일치하지 않습니다.");
		}

		promotion.update(command.description(), command.demoUrl());
		projectPromotionRepository.save(promotion);
		promotionSkillCommandService.update(userId, promotionId, projectId, command.usedSkillIds());
	}

	@Override
	public void delete(Long userId, Long promotionId, Long projectId) {
		ProjectPromotion promotion = checkPromotionExists(promotionId, userId);
		if (!projectId.equals(promotion.getProjectId())) {
			throw new InvalidCommandException("요청 경로의 프로젝트와 홍보글이 속한 프로젝트가 일치하지 않습니다.");
		}
		projectPromotionRepository.deleteById(promotionId);
	}

	private void checkCanCreateProjectPromotion(Long projectId, Long userId){
		checkProjectEnded(projectId);
		checkProjectUser(projectId, userId);
	}

	private void checkProjectEnded(Long projectId){
		ProjectStatus status = projectRepository.getProjectStatus(projectId);
		if(!status.equals(ProjectStatus.FINISHED)) {
			throw new ProjectNotFinishedException(projectId);
		}
	}

	private void checkProjectExists(Long projectId) {
		if(!projectRepository.existsById(projectId)) {
			throw new ProjectNotFoundException(projectId);
		}
	}

	private ProjectPromotion checkPromotionExists(Long promotionId, Long userId) {
		return projectPromotionRepository.findByIdAndUserId(promotionId, userId);
	}

	private void checkProjectUser(Long projectId, Long userId) {
		if (projectUserRepository.findAcceptedByProjectIdAndUserId(projectId, userId).isEmpty()) {
			throw new ProjectUserNotFoundException(projectId);
		}
	}

}
