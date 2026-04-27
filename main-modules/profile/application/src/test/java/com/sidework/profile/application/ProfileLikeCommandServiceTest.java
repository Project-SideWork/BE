package com.sidework.profile.application;

import com.sidework.profile.application.exception.ProfileLikeExistException;
import com.sidework.profile.application.exception.ProfileLikeNotFoundException;
import com.sidework.profile.application.exception.ProfileNotFoundException;
import com.sidework.profile.application.port.out.ProfileLikeOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.application.service.ProfileLikeCommandService;
import com.sidework.profile.domain.ProfileLike;
import com.sidework.user.application.port.in.UserQueryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileLikeCommandServiceTest {

	@Mock
	private ProfileLikeOutPort profileLikeOutPort;

	@Mock
	private ProfileOutPort profileOutPort;

	@Mock
	private UserQueryUseCase userQueryUseCase;

	@InjectMocks
	private ProfileLikeCommandService service;

	@Captor
	private ArgumentCaptor<ProfileLike> profileLikeCaptor;

	@Test
	void 프로필_좋아요에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 2L;
		when(profileOutPort.existsById(profileId)).thenReturn(true);
		when(profileLikeOutPort.isLiked(userId, profileId)).thenReturn(false);

		// when
		service.like(userId, profileId);

		// then
		verify(userQueryUseCase).validateExists(userId);
		verify(profileOutPort).existsById(profileId);
		verify(profileLikeOutPort).isLiked(userId, profileId);
		verify(profileLikeOutPort).like(profileLikeCaptor.capture());
		ProfileLike saved = profileLikeCaptor.getValue();
		assertEquals(userId, saved.getUserId());
		assertEquals(profileId, saved.getProfileId());
	}

	@Test
	void 이미_좋아요한_프로필에_다시_좋아요_시도_시_ProfileLikeExistException을_던진다() {
		// given
		Long userId = 1L;
		Long profileId = 2L;
		when(profileOutPort.existsById(profileId)).thenReturn(true);
		when(profileLikeOutPort.isLiked(userId, profileId)).thenReturn(true);

		// when & then
		assertThrows(
			ProfileLikeExistException.class,
			() -> service.like(userId, profileId)
		);

		verify(userQueryUseCase).validateExists(userId);
		verify(profileOutPort).existsById(profileId);
		verify(profileLikeOutPort).isLiked(userId, profileId);
		verify(profileLikeOutPort, never()).like(any(ProfileLike.class));
	}

	@Test
	void 존재하지_않는_profileId로_좋아요_시도_시_ProfileNotFoundException을_던진다() {
		// given
		Long userId = 1L;
		Long profileId = 2L;
		when(profileOutPort.existsById(profileId)).thenReturn(false);

		// when & then
		assertThrows(
			ProfileNotFoundException.class,
			() -> service.like(userId, profileId)
		);

		verify(userQueryUseCase).validateExists(userId);
		verify(profileOutPort).existsById(profileId);
		verify(profileLikeOutPort, never()).isLiked(anyLong(), anyLong());
		verify(profileLikeOutPort, never()).like(any(ProfileLike.class));
	}

	@Test
	void 프로필_좋아요_취소에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 2L;
		when(profileOutPort.existsById(profileId)).thenReturn(true);
		when(profileLikeOutPort.unlike(userId, profileId)).thenReturn(1);

		// when
		service.delete(userId, profileId);

		// then
		verify(userQueryUseCase).validateExists(userId);
		verify(profileOutPort).existsById(profileId);
		verify(profileLikeOutPort).unlike(userId, profileId);
	}

	@Test
	void 좋아요_내역이_없는데_취소_시도_시_ProfileLikeNotFoundException을_던진다() {
		// given
		Long userId = 1L;
		Long profileId = 2L;
		when(profileOutPort.existsById(profileId)).thenReturn(true);
		when(profileLikeOutPort.unlike(userId, profileId)).thenReturn(0);

		// when & then
		assertThrows(
			ProfileLikeNotFoundException.class,
			() -> service.delete(userId, profileId)
		);

		verify(userQueryUseCase).validateExists(userId);
		verify(profileOutPort).existsById(profileId);
		verify(profileLikeOutPort).unlike(userId, profileId);
	}
}
