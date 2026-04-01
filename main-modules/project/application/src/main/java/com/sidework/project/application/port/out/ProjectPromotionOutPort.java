package com.sidework.project.application.port.out;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sidework.project.application.adapter.ProjectPromotionListResponse;
import com.sidework.project.application.dto.ProjectPromotionDetailRow;
import com.sidework.project.domain.ProjectPromotion;

public interface ProjectPromotionOutPort {
	boolean existsRecentPromotion(Long projectId, Long userId, Instant from);
	Long save(ProjectPromotion projectPromotion);
	ProjectPromotion findByIdAndUserId(Long promotionId, Long userId);

	ProjectPromotion findById(Long promotionId);

	void deleteById(Long promotionId);

	Page<ProjectPromotionListResponse> search(String keyword, List<Long> skillIds, Pageable pageable);
}
