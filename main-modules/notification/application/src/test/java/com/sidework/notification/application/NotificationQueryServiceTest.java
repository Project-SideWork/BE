package com.sidework.notification.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.port.out.NotificationOutPort;
import com.sidework.notification.application.service.NotificationQueryService;
import com.sidework.notification.domain.Notification;
import com.sidework.notification.domain.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

	@Mock
	private NotificationOutPort notificationRepository;

	@InjectMocks
	private NotificationQueryService queryService;

	@Test
	void 유저_알림_목록_조회_시_repository_findByUserId_결과를_Response로_변환해_반환한다() {
		Long userId = 1L;
		List<Notification> notifications = List.of(
			Notification.builder()
				.id(1L)
				.userId(userId)
				.type(NotificationType.PROJECT_APPROVED)
				.title("제목1")
				.body("내용1")
				.isRead(false)
				.build(),
			Notification.builder()
				.id(2L)
				.userId(userId)
				.type(NotificationType.PROJECT_REJECTED)
				.title("제목2")
				.body("내용2")
				.isRead(true)
				.build()
		);
		when(notificationRepository.findByUserId(userId)).thenReturn(notifications);

		List<NotificationResponse> result = queryService.getByUserId(userId);

		verify(notificationRepository).findByUserId(userId);
		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).id());
		assertEquals("제목1", result.get(0).title());
		assertFalse(result.get(0).read());
		assertEquals(2L, result.get(1).id());
		assertEquals("제목2", result.get(1).title());
		assertTrue(result.get(1).read());
	}

	@Test
	void 유저_알림이_없으면_빈_목록을_반환한다() {
		Long userId = 1L;
		when(notificationRepository.findByUserId(userId)).thenReturn(List.of());

		List<NotificationResponse> result = queryService.getByUserId(userId);

		verify(notificationRepository).findByUserId(userId);
		assertEquals(0, result.size());
	}
}
