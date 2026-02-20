package com.sidework.common.event.sse.service;

import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.common.event.sse.port.out.SseSubscribeOutPort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SseSubscribeService implements SseSubscribeUseCase {

	private final SseSubscribeOutPort sseSubscribeOutPort;

    @Override
    public SseEmitter subscribeUser(Long userId) {
        return sseSubscribeOutPort.subscribeUser(userId);
    }

    @Override
    public SseEmitter subscribeChat(Long chatRoomId) {
        return sseSubscribeOutPort.subscribeChatRoom(chatRoomId);
    }
}
