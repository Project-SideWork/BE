package com.sidework.notification.application.port.out;

import java.time.Instant;
import java.util.List;

import com.sidework.notification.domain.Notification;

public record NotificationPage(
	List<Notification> items,
	boolean hasNext,
	Instant nextCursorCreatedAt,
	Long nextCursorId
) {
}
