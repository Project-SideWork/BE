package com.sidework.notification.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	private Long id;
	private Long userId;
	private NotificationType type;
	private String title;
	private String body;
	private boolean isRead;
	private LocalDateTime createdAt;

	public static Notification create(Long userId, NotificationType type, String title, String body) {
		return Notification.builder()
			.userId(userId)
			.type(type)
			.title(title)
			.body(body)
			.isRead(false)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public void markAsRead() {
		this.isRead = true;
	}
}
