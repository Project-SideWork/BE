package com.sidework.skill.persistence.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sidework.skill.application.port.out.ProjectPromotionSkillOutPort;
import com.sidework.skill.domain.ProjectPromotionSkill;
import com.sidework.skill.persistence.entity.ProjectPromotionSkillEntity;
import com.sidework.skill.persistence.mapper.ProjectPromotionSkillMapper;
import com.sidework.skill.persistence.repository.ProjectPromotionSkillJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectPromotionSkillPersistenceAdapter implements ProjectPromotionSkillOutPort {

	private final ProjectPromotionSkillJpaRepository repo;
	private final ProjectPromotionSkillMapper mapper;

	@Override
	public void saveAll(List<ProjectPromotionSkill> domains) {
		List<ProjectPromotionSkillEntity> entities = domains.stream()
			.map(mapper::toEntity)
			.toList();
		repo.saveAll(entities);
	}

	@Override
	public void deleteByPromotionIdAndSkillIdIn(Long promotionId, List<Long> skillIds) {
		if (skillIds == null || skillIds.isEmpty()) {
			return;
		}
		repo.deleteByPromotionIdAndSkillIdIn(promotionId, skillIds);
	}

	@Override
	public List<Long> findAllSkillIdsByPromotionId(Long promotionId) {
		return repo.findAllSkillIdsByPromotionId(promotionId);
	}

	@Override
	public Map<Long, List<String>> findSkillNamesByPromotionIdIn(List<Long> promotionIds) {
		if (promotionIds == null || promotionIds.isEmpty()) {
			return Map.of();
		}
		List<Object[]> rows = repo.findPromotionIdAndSkillNameByPromotionIdIn(promotionIds);
		Map<Long, List<String>> map = new LinkedHashMap<>();
		for (Object[] row : rows) {
			Long promotionId = (Long) row[0];
			String name = (String) row[1];
			map.computeIfAbsent(promotionId, k -> new ArrayList<>()).add(name);
		}
		return map;
	}
}
