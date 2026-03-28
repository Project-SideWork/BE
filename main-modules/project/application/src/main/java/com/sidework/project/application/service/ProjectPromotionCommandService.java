package com.sidework.project.application.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.dto.ProjectPromotionCommand;
import com.sidework.project.application.exception.AlreadyPromotedException;
import com.sidework.project.application.exception.ProjectNotFinishedException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectPromotionCommandUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.domain.ProjectStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectPromotionCommandService implements ProjectPromotionCommandUseCase {
	private final ProjectOutPort projectRepository;
	private final ProjectPromotionOutPort projectPromotionRepository;


	@Override
	public void create(Long userId, Long projectId, ProjectPromotionCommand command) {
		checkProjectExists(projectId);
		checkCanCreateProjectPromotion(projectId, userId);
		ProjectPromotion promotion = ProjectPromotion.create(projectId, userId, command.description(), command.demoUrl());
		projectPromotionRepository.save(promotion);

	}

	private void checkCanCreateProjectPromotion(Long projectId, Long userId){
		checkProjectEnded(projectId);
		checkProjectPromotionExists(projectId, userId);
	}

	private void checkProjectEnded(Long projectId){
		ProjectStatus status = projectRepository.getProjectStatus(projectId);
		if(!status.equals(ProjectStatus.FINISHED)) {
			throw new ProjectNotFinishedException(projectId);
		}
	}

	private void checkProjectPromotionExists(Long projectId, Long userId){
		Instant limit = Instant.now().minus(24, ChronoUnit.HOURS);

		boolean exists = projectPromotionRepository.existsRecentPromotion(projectId, userId,limit);

		if (exists) {
			throw new AlreadyPromotedException();
		}
	}

	private void checkProjectExists(Long projectId) {
		if(!projectRepository.existsById(projectId)) {
			throw new ProjectNotFoundException(projectId);
		}
	}
}
