package com.sidework.notification.application.adapter;

public record FcmTestRequest(
	Long userId,
	String title,
	String body
) {
}
