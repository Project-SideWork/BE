package com.sidework.project.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;

import com.sidework.project.application.exception.ProjectPromotionNotFoundException;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.persistence.entity.ProjectPromotionEntity;
import com.sidework.project.persistence.mapper.ProjectPromotionMapper;
import com.sidework.project.persistence.repository.ProjectPromotionJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectPromotionPersistenceAdapter implements ProjectPromotionOutPort {

	private final ProjectPromotionJpaRepository projectPromotionJpaRepository;
	private final ProjectPromotionMapper mapper;

	@Override
	public boolean existsRecentPromotion(Long projectId, Long userId, Instant from) {
		return projectPromotionJpaRepository.existsRecentPromotion(projectId, userId, from);
	}

	@Override
	public Long save(ProjectPromotion projectPromotion) {
		return projectPromotionJpaRepository.save(mapper.toEntity(projectPromotion)).getId();
	}

	@Override
	public ProjectPromotion findByIdAndUserId(Long promotionId, Long userId) {
		ProjectPromotionEntity entity = projectPromotionJpaRepository.findByIdAndUserId(promotionId, userId)
			.orElseThrow(() -> new ProjectPromotionNotFoundException(promotionId, userId));
		return mapper.toDomain(entity);
	}
}
