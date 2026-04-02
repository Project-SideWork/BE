package com.sidework.skill.application.port.out;

import java.util.List;
import java.util.Map;

import com.sidework.skill.domain.ProjectPromotionSkill;

public interface ProjectPromotionSkillOutPort {
	void saveAll(List<ProjectPromotionSkill> domains);

	void deleteByPromotionIdAndSkillIdIn(Long promotionId, List<Long> skillIds);

	List<Long> findAllSkillIdsByPromotionId(Long promotionId);

	Map<Long, List<String>> findSkillNamesByPromotionIdIn(List<Long> promotionIds);
}
