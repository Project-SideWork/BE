package com.sidework.notification.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.common.response.CursorResponse;
import com.sidework.common.util.CursorUtil;
import com.sidework.common.util.CursorWrapper;
import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.port.in.NotificationQueryUseCase;
import com.sidework.notification.application.port.out.NotificationOutPort;
import com.sidework.notification.application.port.out.NotificationPage;
import com.sidework.notification.domain.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationQueryService implements NotificationQueryUseCase {

	private static final int DEFAULT_PAGE_SIZE = 10;

	private final NotificationOutPort notificationRepository;

	@Override
	public CursorResponse<NotificationResponse> getByUserId(Long userId, String cursor, int size) {
		int pageSize = size > 0 ? Math.min(size, 100) : DEFAULT_PAGE_SIZE;
		CursorWrapper decoded = CursorUtil.decode(cursor);
		NotificationPage page = notificationRepository.findByUserIdAndCursor(
			userId, decoded.cursorCreatedAt(), decoded.cursorId(), pageSize);

		List<NotificationResponse> content = page.items().stream()
			.map(this::toResponse)
			.toList();

		String nextCursor = null;
		if (page.hasNext() && page.nextCursorCreatedAt() != null && page.nextCursorId() != null) {
			nextCursor = CursorUtil.encode(new CursorWrapper(
				page.nextCursorCreatedAt(), page.nextCursorId()));
		}

		return new CursorResponse<>(content, nextCursor, page.hasNext());
	}

	private NotificationResponse toResponse(Notification notification) {
		return NotificationResponse.from(notification);
	}
}
