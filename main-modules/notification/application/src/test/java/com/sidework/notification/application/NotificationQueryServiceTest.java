package com.sidework.notification.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.sidework.notification.application.port.out.NotificationPage;
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
	void 유저_알림_목록_조회_시_findByUserIdAndCursor_결과를_CursorResponse로_반환한다() {
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
		NotificationPage page = new NotificationPage(notifications, false, null, null);
		when(notificationRepository.findByUserIdAndCursor(eq(userId), any(), any(), eq(10)))
			.thenReturn(page);

		var result = queryService.getByUserId(userId, null, 10);

		verify(notificationRepository).findByUserIdAndCursor(eq(userId), any(), any(), eq(10));
		assertEquals(2, result.content().size());
		assertEquals(1L, result.content().get(0).id());
		assertEquals("제목1", result.content().get(0).title());
		assertFalse(result.content().get(0).read());
		assertEquals(2L, result.content().get(1).id());
		assertEquals("제목2", result.content().get(1).title());
		assertTrue(result.content().get(1).read());
		assertFalse(result.hasNext());
	}

	@Test
	void 유저_알림이_없으면_빈_content를_반환한다() {
		Long userId = 1L;
		NotificationPage page = new NotificationPage(List.of(), false, null, null);
		when(notificationRepository.findByUserIdAndCursor(eq(userId), any(), any(), eq(10)))
			.thenReturn(page);

		var result = queryService.getByUserId(userId, null, 10);

		verify(notificationRepository).findByUserIdAndCursor(eq(userId), any(), any(), eq(10));
		assertEquals(0, result.content().size());
		assertFalse(result.hasNext());
	}
}
