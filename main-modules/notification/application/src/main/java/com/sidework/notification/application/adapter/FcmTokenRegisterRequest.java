package com.sidework.notification.application.adapter;

public record FcmTokenRegisterRequest(
	String token,
	boolean pushAgreed
) {
}
