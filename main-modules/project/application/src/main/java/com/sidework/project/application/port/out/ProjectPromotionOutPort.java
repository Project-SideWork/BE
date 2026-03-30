package com.sidework.project.application.port.out;

import java.time.Instant;

import com.sidework.project.domain.ProjectPromotion;

public interface ProjectPromotionOutPort {
	boolean existsRecentPromotion(Long projectId, Long userId, Instant from);
	Long save(ProjectPromotion projectPromotion);
	ProjectPromotion findByIdAndUserId(Long promotionId, Long userId);

	void deleteById(Long promotionId);
}
