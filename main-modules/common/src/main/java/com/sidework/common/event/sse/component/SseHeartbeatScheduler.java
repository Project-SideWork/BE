package com.sidework.common.event.sse.component;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

	private static final long HEARTBEAT_INTERVAL_MS = 20_000L; 

	private final List<SseEmitterManager> managers;

	@Scheduled(fixedRate = HEARTBEAT_INTERVAL_MS)
	public void sendHeartbeat() {
        for (SseEmitterManager manager : managers) {
            manager.sendHeartbeat();
        }
	}
}
