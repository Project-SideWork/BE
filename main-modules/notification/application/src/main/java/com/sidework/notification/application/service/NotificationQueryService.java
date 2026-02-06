package com.sidework.notification.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.port.in.NotificationQueryUseCase;
import com.sidework.notification.application.port.out.NotificationOutPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationQueryService implements NotificationQueryUseCase {

	private final NotificationOutPort notificationRepository;

	@Override
	public List<NotificationResponse> getByUserId(Long userId) {
		return notificationRepository.findByUserId(userId)
			.stream()
			.map(NotificationResponse::from)
			.toList();
	}
}
