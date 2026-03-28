package com.sidework.skill.application.port.in;

import java.util.List;

public interface ProjectPromotionSkillCommandUseCase {
	void create(Long promotionId, List<Long> skillIds);
}
