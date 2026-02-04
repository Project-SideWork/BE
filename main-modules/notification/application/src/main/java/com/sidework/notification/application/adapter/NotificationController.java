package com.sidework.notification.application.adapter;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sidework.common.response.ApiResponse;
import com.sidework.notification.application.port.in.NotificationCommand;
import com.sidework.notification.application.port.in.NotificationCommandUseCase;
import com.sidework.notification.application.port.in.NotificationQueryUseCase;
import com.sidework.notification.application.port.out.SseSubscribeOutPort;
import com.sidework.notification.domain.NotificationType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationCommandUseCase commandService;
	private final NotificationQueryUseCase queryService;
	private final SseSubscribeOutPort sseSubscribeOutPort;

	//TODO: 로그인 연동
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe() {
		Long userId = 1L;
		return sseSubscribeOutPort.subscribe(userId);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<NotificationResponse>>> list() {
		Long userId = 1L;
		return ResponseEntity.ok(ApiResponse.onSuccess(queryService.getByUserId(userId)));
	}

	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
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

}
