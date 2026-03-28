package com.sidework.project.persistence.adapter;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.domain.ProjectPromotion;
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
	public void save(ProjectPromotion projectPromotion) {
		projectPromotionJpaRepository.save(mapper.toEntity(projectPromotion));
	}
}
