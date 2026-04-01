package com.sidework.skill.application.port.in;

import java.util.List;

public interface ProjectPromotionSkillQueryUseCase {
	List<String> queryNamesByPromotionId(Long promotionId);
}
