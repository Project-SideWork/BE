package com.sidework.notification.persistence.sse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

	private static final long HEARTBEAT_INTERVAL_MS = 20_000L; 

	private final SseEmitterManager sseEmitterManager;

	@Scheduled(fixedRate = HEARTBEAT_INTERVAL_MS)
	public void sendHeartbeat() {
		sseEmitterManager.sendHeartbeat();
	}
}
