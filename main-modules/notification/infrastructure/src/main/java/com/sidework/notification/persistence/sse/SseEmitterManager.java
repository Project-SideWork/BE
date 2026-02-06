package com.sidework.notification.persistence.sse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SseEmitterManager {

	private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30분

	private final Map<Long, List<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();

	public SseEmitter subscribe(Long userId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		emitter.onCompletion(() -> {
			removeEmitter(userId, emitter);
			log.debug("SSE completed for userId={}", userId);
		});
		emitter.onTimeout(() -> {
			removeEmitter(userId, emitter);
			log.debug("SSE timeout for userId={}", userId);
		});
		emitter.onError(ex -> {
			removeEmitter(userId, emitter);
			log.debug("SSE error for userId={}", userId, ex);
		});

		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("connected"));
		} catch (IOException e) {
			log.debug("Failed to send connect event for userId={}", userId, e);
			emitter.completeWithError(e);
			return emitter;
		}

		emittersByUser.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
		log.debug("SSE subscribed userId={}, connections={}", userId, emittersByUser.get(userId).size());
		return emitter;
	}

	private void removeEmitter(Long userId, SseEmitter emitter) {
		emittersByUser.compute(userId, (k, list) -> {
			if (list == null) return null;
			list.remove(emitter);
			return list.isEmpty() ? null : list;
		});
	}

	public void sendToUser(Long userId, Object data) {
		List<SseEmitter> list = emittersByUser.get(userId);
		if (list == null || list.isEmpty()) {
			log.debug("No SSE connection for userId={}", userId);
			return;
		}
		for (SseEmitter emitter : list) {
			try {
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(data));
			} catch (IOException e) {
				log.debug("SSE disconnected userId={}", userId);
				emitter.completeWithError(e);
			}
		}
	}

	public void sendHeartbeat() {
		for (Map.Entry<Long, List<SseEmitter>> entry : emittersByUser.entrySet()) {
			Long userId = entry.getKey();
			List<SseEmitter> list = entry.getValue();
			for (SseEmitter emitter : list) {
				try {
					emitter.send(SseEmitter.event()
						.name("ping")
						.data("ping"));
				} catch (IOException e) {
					log.debug("Heartbeat failed for userId={}", userId, e);
					emitter.completeWithError(e);
				}
			}
		}
	}
}
