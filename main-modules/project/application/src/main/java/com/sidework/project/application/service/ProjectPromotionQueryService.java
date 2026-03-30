package com.sidework.project.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.ProjectPromotionListResponse;
import com.sidework.project.application.port.in.ProjectPromotionQueryUseCase;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectPromotionQueryService implements ProjectPromotionQueryUseCase {

	private final ProjectPromotionOutPort projectPromotionOutPort;

	@Override
	public PageResponse<List<ProjectPromotionListResponse>> queryProjectPromotionList(String keyword, List<Long> skillIds, Pageable pageable) {
		Page<ProjectPromotionListResponse> page = projectPromotionOutPort.search(keyword, skillIds, pageable);
		return PageResponse.from(page, page.getContent());
	}
}
