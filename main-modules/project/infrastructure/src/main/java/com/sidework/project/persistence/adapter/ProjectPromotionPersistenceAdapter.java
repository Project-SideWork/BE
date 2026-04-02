package com.sidework.project.persistence.adapter;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.sidework.project.application.dto.ProjectPromotionListRow;
import com.sidework.project.application.exception.ProjectPromotionNotFoundException;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.persistence.entity.ProjectPromotionEntity;
import com.sidework.project.persistence.mapper.ProjectPromotionMapper;
import com.sidework.project.persistence.repository.ProjectPromotionJpaRepository;
import com.sidework.project.persistence.repository.condition.ProjectPromotionSearchCondition;

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
			.orElseThrow(() -> new ProjectPromotionNotFoundException(promotionId));
		return mapper.toDomain(entity);
	}

	@Override
	public void deleteById(Long promotionId) {
		projectPromotionJpaRepository.deleteById(promotionId);
	}

	@Override
	public Page<ProjectPromotionListRow> search(String keyword, List<Long> skillIds, Pageable pageable) {
		ProjectPromotionSearchCondition condition = new ProjectPromotionSearchCondition();
		condition.setKeyword(keyword);
		condition.setSkillIds(skillIds);
		condition.setSkillCount(skillIds == null ? 0L : (long) skillIds.size());

		return projectPromotionJpaRepository.searchPromotions(condition, pageable);
	}

	@Override
	public ProjectPromotion findById(Long promotionId) {
		ProjectPromotionEntity entity = projectPromotionJpaRepository.findById(promotionId)
			.orElseThrow(() -> new ProjectPromotionNotFoundException(promotionId));
		return mapper.toDomain(entity);
	}
}
