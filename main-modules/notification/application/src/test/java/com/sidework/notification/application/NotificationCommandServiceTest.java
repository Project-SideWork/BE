package com.sidework.notification.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sidework.notification.application.exception.NotificationNotFoundException;
import com.sidework.notification.application.port.out.NotificationOutPort;
import com.sidework.notification.application.port.out.NotificationSendOutPort;
import com.sidework.notification.application.service.NotificationCommandService;
import com.sidework.notification.domain.Notification;
import com.sidework.notification.domain.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

	@Mock
	private NotificationOutPort notificationRepository;

	@Mock
	private NotificationSendOutPort notificationSendRepository;

	@InjectMocks
	private NotificationCommandService commandService;

	@Captor
	private ArgumentCaptor<Notification> notificationCaptor;

	@Test
	void 알림_생성_시_save와_send가_호출된다() {
		Long userId = 1L;
		NotificationType type = NotificationType.PROJECT_APPROVED;
		String title = "제목";
		String body = "내용";

		commandService.create(userId, type, title, body);

		verify(notificationRepository).save(notificationCaptor.capture());
		Notification saved = notificationCaptor.getValue();
		assertEquals(userId, saved.getUserId());
		assertEquals(type, saved.getType());
		assertEquals(title, saved.getTitle());
		assertEquals(body, saved.getBody());
		assertEquals(false, saved.isRead());

		verify(notificationSendRepository).send(eq(userId), any(Notification.class));
	}

	@Test
	void 알림_읽음_처리_시_본인_알림이면_update가_호출된다() {
		Long notificationId = 1L;
		Long userId = 1L;
		Notification notification = Notification.builder()
			.id(notificationId)
			.userId(userId)
			.type(NotificationType.PROJECT_APPROVED)
			.title("제목")
			.body("내용")
			.isRead(false)
			.build();
		when(notificationRepository.findById(notificationId)).thenReturn(notification);

		commandService.markAsRead(notificationId, userId);

		verify(notificationRepository).update(notificationCaptor.capture());
		Notification updated = notificationCaptor.getValue();
		assertEquals(true, updated.isRead());
	}

	@Test
	void 알림_읽음_처리_시_알림이_없으면_NotificationNotFoundException을_던진다() {
		Long notificationId = 999L;
		Long userId = 1L;
		when(notificationRepository.findById(notificationId)).thenReturn(null);

		assertThrows(NotificationNotFoundException.class,
			() -> commandService.markAsRead(notificationId, userId));
	}

	@Test
	void 알림_읽음_처리_시_다른_유저의_알림이면_NotificationNotFoundException을_던진다() {
		Long notificationId = 1L;
		Long ownerId = 1L;
		Long otherUserId = 2L;
		Notification notification = Notification.builder()
			.id(notificationId)
			.userId(ownerId)
			.type(NotificationType.PROJECT_APPROVED)
			.title("제목")
			.body("내용")
			.isRead(false)
			.build();
		when(notificationRepository.findById(notificationId)).thenReturn(notification);

		assertThrows(NotificationNotFoundException.class,
			() -> commandService.markAsRead(notificationId, otherUserId));
	}
}
