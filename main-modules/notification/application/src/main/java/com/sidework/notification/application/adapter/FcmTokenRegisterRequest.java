package com.sidework.notification.application.adapter;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRegisterRequest(
	@NotBlank String token,
	boolean pushAgreed
) {
}
