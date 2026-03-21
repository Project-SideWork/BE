package com.sidework.project.application;

import com.sidework.project.application.port.out.ProjectLikeOutPort;
import com.sidework.project.application.service.ProjectLikeQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectLikeQueryServiceTest {

	@Mock
	private ProjectLikeOutPort projectLikeOutPort;

	@InjectMocks
	private ProjectLikeQueryService service;

	@Test
	void 프로젝트_ID_목록에_대한_좋아요_여부를_조회한다() {
		// given
		Long userId = 1L;
		List<Long> projectIds = List.of(10L, 20L);
		Map<Long, Boolean> expected = Map.of(10L, true, 20L, false);
		when(projectLikeOutPort.getLikes(userId, projectIds)).thenReturn(expected);

		// when
		Map<Long, Boolean> result = service.isLikedByProjectIds(userId, projectIds);

		// then
		assertEquals(expected, result);
		verify(projectLikeOutPort).getLikes(userId, projectIds);
	}

	@Test
	void 좋아요한_프로젝트_ID_목록을_조회한다() {
		// given
		Long userId = 1L;
		List<Long> expected = List.of(10L, 20L);
		when(projectLikeOutPort.findLikedProjectIds(userId)).thenReturn(expected);

		// when
		List<Long> result = service.findLikedProjectIds(userId);

		// then
		assertEquals(expected, result);
		verify(projectLikeOutPort).findLikedProjectIds(userId);
	}
}
