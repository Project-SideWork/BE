package com.sidework.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
	PROJECT_APPROVED("프로젝트 승인"),
	PROJECT_REJECTED("프로젝트 거절");
	private final String value;
}
