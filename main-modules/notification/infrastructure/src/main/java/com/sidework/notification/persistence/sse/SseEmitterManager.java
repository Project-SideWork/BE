package com.sidework.notification.persistence.sse;

import java.io.IOException;
import java.util.ArrayList;
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

		emittersByUser.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("connected"));
			log.debug("SSE subscribed userId={}, connections={}", userId, emittersByUser.get(userId).size());
		} catch (IOException e) {
			removeEmitter(userId, emitter);
			log.debug("Failed to send connect event for userId={}", userId, e);
		}
		return emitter;
	}

	private void removeEmitter(Long userId, SseEmitter emitter) {
		List<SseEmitter> list = emittersByUser.get(userId);
		if (list != null) {
			list.remove(emitter);
			if (list.isEmpty()) {
				emittersByUser.remove(userId);
			}
		}
	}

	public void sendToUser(Long userId, Object data) {
		List<SseEmitter> list = emittersByUser.get(userId);
		if (list == null || list.isEmpty()) {
			log.debug("No SSE connection for userId={}", userId);
			return;
		}
		List<SseEmitter> failed = new ArrayList<>();
		for (SseEmitter emitter : list) {
			try {
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(data));
			} catch (IOException e) {
				failed.add(emitter);
				log.debug("SSE disconnected userId={}", userId);
			}
		}
		list.removeAll(failed);
		if (list.isEmpty()) {
			emittersByUser.remove(userId);
		}
	}

	public void sendHeartbeat() {
		for (Map.Entry<Long, List<SseEmitter>> entry : emittersByUser.entrySet()) {
			Long userId = entry.getKey();
			List<SseEmitter> list = entry.getValue();
			List<SseEmitter> failed = new ArrayList<>();
			for (SseEmitter emitter : list) {
				try {
					emitter.send(SseEmitter.event()
						.name("ping")
						.data("ping"));
				} catch (IOException e) {
					failed.add(emitter);
					log.debug("Heartbeat failed for userId={}", userId, e);
				}
			}
			list.removeAll(failed);
			if (list.isEmpty()) {
				emittersByUser.remove(userId);
			}
		}
	}
}
