package com.sidework.common.event.sse.component;

import com.sidework.common.event.sse.port.out.SseSubscribeOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Component
@RequiredArgsConstructor
public class SseSubscribeAdapter implements SseSubscribeOutPort {

    private final UserSseEmitter userSseEmitter;
    private final ChatRoomSseEmitter chatRoomSseEmitter;

    @Override
    public SseEmitter subscribeUser(Long userId) {
        return userSseEmitter.subscribe(userId);
    }

    @Override
    public SseEmitter subscribeChatRoom(Long chatRoomId) {
        return chatRoomSseEmitter.subscribe(chatRoomId);
    }
}
