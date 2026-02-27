package com.sidework.common.event.sse.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatRoomSseEmitter extends SseEmitterManager {
    private final Map<Long, List<SseEmitter>> emittersByRoom = new ConcurrentHashMap<>();

    @Override
    protected Map<Long, List<SseEmitter>> getStorage() {
        return emittersByRoom;
    }

    @Override
    protected String getLogPrefix() {
        return "chatRoomId";
    }
}
