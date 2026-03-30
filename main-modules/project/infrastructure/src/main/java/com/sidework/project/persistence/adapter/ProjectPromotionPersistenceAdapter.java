package com.sidework.project.persistence.adapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.sidework.project.application.adapter.ProjectPromotionListResponse;
import com.sidework.project.application.dto.ProjectPromotionListRow;
import com.sidework.project.application.exception.ProjectPromotionNotFoundException;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.persistence.entity.ProjectPromotionEntity;
import com.sidework.project.persistence.mapper.ProjectPromotionMapper;
import com.sidework.project.persistence.repository.ProjectPromotionJpaRepository;
import com.sidework.project.persistence.repository.condition.ProjectPromotionSearchCondition;
import com.sidework.skill.persistence.repository.ProjectPromotionSkillJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectPromotionPersistenceAdapter implements ProjectPromotionOutPort {

	private final ProjectPromotionJpaRepository projectPromotionJpaRepository;
	private final ProjectPromotionSkillJpaRepository projectPromotionSkillJpaRepository;
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

	@Override
	public void deleteById(Long promotionId) {
		projectPromotionJpaRepository.deleteById(promotionId);
	}

	@Override
	public Page<ProjectPromotionListResponse> search(String keyword, List<Long> skillIds, Pageable pageable) {
		ProjectPromotionSearchCondition condition = new ProjectPromotionSearchCondition();
		condition.setKeyword(keyword);
		condition.setSkillIds(skillIds);
		condition.setSkillCount(skillIds == null ? 0L : (long) skillIds.size());

		Page<ProjectPromotionListRow> page = projectPromotionJpaRepository.searchPromotions(condition, pageable);

		List<Long> promotionIds = page.getContent().stream()
			.map(ProjectPromotionListRow::promotionId)
			.toList();

		Map<Long, List<String>> stacksByPromotionId = loadSkillNamesByPromotionIds(promotionIds);

		return page.map(row -> new ProjectPromotionListResponse(
			row.projectId(),
			row.title(),
			row.promotionDescription(),
			stacksByPromotionId.getOrDefault(row.promotionId(), List.of())
		));
	}

	private Map<Long, List<String>> loadSkillNamesByPromotionIds(List<Long> promotionIds) {
		if (promotionIds == null || promotionIds.isEmpty()) {
			return Map.of();
		}

		List<Object[]> rows = projectPromotionSkillJpaRepository.findPromotionIdAndSkillNameByPromotionIdIn(promotionIds);
		Map<Long, List<String>> map = new LinkedHashMap<>();
		for (Object[] row : rows) {
			Long promotionId = (Long) row[0];
			String name = (String) row[1];
			map.computeIfAbsent(promotionId, k -> new ArrayList<>()).add(name);
		}
		return map;
	}
}
