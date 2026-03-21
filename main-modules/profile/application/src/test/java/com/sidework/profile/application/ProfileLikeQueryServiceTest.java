package com.sidework.profile.application;

import com.sidework.profile.application.port.out.ProfileLikeOutPort;
import com.sidework.profile.application.service.ProfileLikeQueryService;
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
class ProfileLikeQueryServiceTest {

	@Mock
	private ProfileLikeOutPort profileLikeOutPort;

	@InjectMocks
	private ProfileLikeQueryService service;

	@Test
	void 프로필_ID_목록에_대한_좋아요_여부를_조회한다() {
		// given
		Long userId = 1L;
		List<Long> profileIds = List.of(10L, 20L);
		Map<Long, Boolean> expected = Map.of(10L, true, 20L, false);
		when(profileLikeOutPort.getLikes(userId, profileIds)).thenReturn(expected);

		// when
		Map<Long, Boolean> result = service.isLikedByProfileIds(userId, profileIds);

		// then
		assertEquals(expected, result);
		verify(profileLikeOutPort).getLikes(userId, profileIds);
	}

	@Test
	void 좋아요한_프로필_ID_목록을_조회한다() {
		// given
		Long userId = 1L;
		List<Long> expected = List.of(10L, 20L);
		when(profileLikeOutPort.findLikedProfileIds(userId)).thenReturn(expected);

		// when
		List<Long> result = service.findLikedProfileIds(userId);

		// then
		assertEquals(expected, result);
		verify(profileLikeOutPort).findLikedProfileIds(userId);
	}
}
