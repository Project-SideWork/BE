package com.sidework.notification.application.port.in;

public record NotificationCommand(
	String title,
	String body
) {
}
