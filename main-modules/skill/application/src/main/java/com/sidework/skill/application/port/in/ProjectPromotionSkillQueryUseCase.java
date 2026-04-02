package com.sidework.skill.application.port.in;

import java.util.List;
import java.util.Map;

public interface ProjectPromotionSkillQueryUseCase {
	List<String> queryNamesByPromotionId(Long promotionId);

	Map<Long, List<String>> queryNamesByPromotionIds(List<Long> promotionIds);
}
