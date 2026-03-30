package com.sidework.skill.persistence.adapter;

import java.util.List;

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
}
