package com.sidework.notification.application.adapter;

import java.util.List;

import com.sidework.common.auth.AuthenticatedUserDetails;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sidework.common.response.ApiResponse;
import com.sidework.notification.application.port.in.FcmPushUseCase;
import com.sidework.notification.application.port.in.FcmTokenCommandUseCase;
import com.sidework.notification.application.port.in.NotificationCommand;
import com.sidework.notification.application.port.in.NotificationCommandUseCase;
import com.sidework.notification.application.port.in.NotificationQueryUseCase;
import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.notification.domain.NotificationType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationCommandUseCase commandService;
	private final NotificationQueryUseCase queryService;
	private final SseSubscribeUseCase sseSubscribeUseCase;
	private final FcmTokenCommandUseCase fcmTokenCommandUseCase;
	private final FcmPushUseCase fcmPushService;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@AuthenticationPrincipal AuthenticatedUserDetails user) {
		return sseSubscribeUseCase.subscribeUser(user.getId());
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<NotificationResponse>>> list() {
		Long userId = 1L;
		return ResponseEntity.ok(ApiResponse.onSuccess(queryService.getByUserId(userId)));
	}

	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable("notificationId") Long notificationId) {
		Long userId = 1L;
		commandService.markAsRead(notificationId, userId);
		return ResponseEntity.ok(ApiResponse.onSuccessVoid());
	}

	@PostMapping("/test")
	public ResponseEntity<ApiResponse<Void>> sendTest(@RequestBody NotificationCommand request) {
		Long userId = 1L;
		commandService.create(userId, NotificationType.PROJECT_APPROVED, request.title(), request.body());
		return ResponseEntity.ok(ApiResponse.onSuccessVoid());
	}

	@PostMapping("/fcm-token")
	public ResponseEntity<ApiResponse<Void>> registerFcmToken(@RequestBody @Valid FcmTokenRegisterRequest request) {
		Long userId = 1L;
		fcmTokenCommandUseCase.registerToken(userId, request.token(), request.pushAgreed());
		return ResponseEntity.ok(ApiResponse.onSuccessVoid());
	}

	@PostMapping("/fcm-test")
	public ResponseEntity<ApiResponse<Void>> sendFcmTest(@RequestBody(required = false) FcmTestRequest request) {
		long userId = request != null && request.userId() != null ? request.userId() : 1L;
		String title = request != null && request.title() != null && !request.title().isBlank()
			? request.title() : "FCM 테스트";
		String body = request != null && request.body() != null && !request.body().isBlank()
			? request.body() : "푸시 알림 테스트 메시지입니다.";
		fcmPushService.sendToUser(userId, title, body);
		return ResponseEntity.ok(ApiResponse.onSuccessVoid());
	}
}
