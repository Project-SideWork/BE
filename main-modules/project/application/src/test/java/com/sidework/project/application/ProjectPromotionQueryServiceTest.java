package com.sidework.project.application;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.ProjectPromotionListResponse;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.application.service.ProjectPromotionQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectPromotionQueryServiceTest {

	@Mock
	private ProjectPromotionOutPort projectPromotionOutPort;

	@InjectMocks
	private ProjectPromotionQueryService service;

	@Test
	void 홍보글_목록을_조회하면_PageResponse로_반환한다() {
		String keyword = "키워드";
		List<Long> skillIds = List.of(1L, 2L);
		Pageable pageable = PageRequest.of(0, 20);
		List<ProjectPromotionListResponse> rows = List.of(
			new ProjectPromotionListResponse(1L, "제목", "설명", List.of("Java", "Spring"))
		);
		Page<ProjectPromotionListResponse> page = new PageImpl<>(rows, pageable, 1L);
		when(projectPromotionOutPort.search(eq(keyword), eq(skillIds), eq(pageable))).thenReturn(page);

		PageResponse<List<ProjectPromotionListResponse>> result =
			service.queryProjectPromotionList(keyword, skillIds, pageable);

		assertEquals(1, result.content().size());
		assertEquals(1L, result.content().get(0).projectId());
		assertEquals("제목", result.content().get(0).title());
		assertEquals("설명", result.content().get(0).description());
		assertEquals(List.of("Java", "Spring"), result.content().get(0).usedStacks());
		assertEquals(1, result.page());
		assertEquals(20, result.size());
		assertEquals(1L, result.totalElements());
		assertEquals(1, result.totalPages());
		verify(projectPromotionOutPort).search(keyword, skillIds, pageable);
	}
}
