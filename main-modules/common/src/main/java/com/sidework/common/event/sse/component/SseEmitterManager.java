package com.sidework.common.event.sse.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public abstract class SseEmitterManager {
    protected static final long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30분

    protected abstract Map<Long, List<SseEmitter>> getStorage();

    protected abstract String getLogPrefix(); // 로그 구분용

    public SseEmitter subscribe(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 1. 먼저 storage에 등록
        List<SseEmitter> list = getStorage().compute(id, (k, existing) -> {
            List<SseEmitter> l = existing != null ? existing : new CopyOnWriteArrayList<>();
            l.add(emitter);
            return l;
        });

        // 2. 등록 후 콜백 설정
        emitter.onCompletion(() -> removeEmitter(id, emitter));
        emitter.onTimeout(() -> {
            removeEmitter(id, emitter);
            emitter.complete();
        });
        emitter.onError(ex -> removeEmitter(id, emitter));

        // 3. connect 이벤트 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (Exception e) {
            log.debug("Failed to send connect event for {}={}", getLogPrefix(), id, e);
            removeEmitter(id, emitter);
            emitter.completeWithError(e);
        }

        log.debug("SSE subscribed {}={}, connections={}", getLogPrefix(), id, list.size());
        return emitter;
    }

    public void removeEmitter(Long id, SseEmitter emitter) {
        getStorage().compute(id, (k, list) -> {
            if (list == null) return null;
            list.remove(emitter);
            return list.isEmpty() ? null : list;
        });
    }

    public void sendTo(Long id, Object data) {
        List<SseEmitter> list = getStorage().get(id);
        if (list == null || list.isEmpty()) {
            log.debug("No SSE connection for {}={}", getLogPrefix(), id);
            return;
        }

        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
            } catch (Exception e) {
                log.debug("Failed to send SSE notification for {}={}", getLogPrefix(), id, e);
                removeEmitter(id, emitter);
                emitter.completeWithError(e);
            }
        }
    }

    public void sendHeartbeat() {
        for (Map.Entry<Long, List<SseEmitter>> entry : getStorage().entrySet()) {
            Long id = entry.getKey();

            for (SseEmitter emitter : entry.getValue()) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("ping")
                            .data("ping"));
                } catch (Exception e) {
                    log.debug("Failed to send SSE heartbeat for {}={}", getLogPrefix(), id, e);
                    removeEmitter(id, emitter);
                    emitter.completeWithError(e);
                }
            }
        }
    }
}
