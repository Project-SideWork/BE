package com.sidework.notification.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.exception.NotificationNotFoundException;
import com.sidework.notification.application.port.in.NotificationCommandUseCase;
import com.sidework.notification.application.port.out.NotificationOutPort;
import com.sidework.notification.application.port.out.NotificationSendOutPort;
import com.sidework.notification.domain.Notification;
import com.sidework.notification.domain.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
@Slf4j
public class NotificationCommandService implements NotificationCommandUseCase {

	private final NotificationOutPort notificationRepository;
	private final NotificationSendOutPort notificationSendRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Long userId, NotificationType type, String title, String body) {
		Notification notification = Notification.create(
			userId,
			type,
			title,
			body
		);
		notificationRepository.save(notification);
		notificationSendRepository.send(userId, notification);
	}

	@Override
	public void markAsRead(Long notificationId, Long userId) {
		Notification notification = notificationRepository.findById(notificationId);
		if (notification == null || !Objects.equals(notification.getUserId(), userId)) {
			throw new NotificationNotFoundException(notificationId);
		}
		notification.markAsRead();
		notificationRepository.update(notification);
	}
}
