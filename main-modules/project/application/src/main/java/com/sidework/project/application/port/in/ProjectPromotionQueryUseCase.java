package com.sidework.project.application.port.in;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.ProjectPromotionDetailResponse;
import com.sidework.project.application.adapter.ProjectPromotionListResponse;

public interface ProjectPromotionQueryUseCase {

	PageResponse<List<ProjectPromotionListResponse>> queryProjectPromotionList(String keyword, List<Long> skillIds, Pageable pageable);
	ProjectPromotionDetailResponse queryProjectPromotionDetail(Long promotionId, Long projectId);

}
