package com.sidework.skill.application.port.in;

import java.util.List;

public interface ProjectPromotionSkillCommandUseCase {
	void create(Long userId, Long promotionId, Long projectId, List<Long> skillIds);
}
