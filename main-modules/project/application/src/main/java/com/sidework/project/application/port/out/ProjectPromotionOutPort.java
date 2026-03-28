package com.sidework.project.application.port.out;

import java.time.Instant;

import com.sidework.project.domain.ProjectPromotion;

public interface ProjectPromotionOutPort {
	boolean existsRecentPromotion(Long projectId, Long userId, Instant from);
	void save(ProjectPromotion projectPromotion);
}
