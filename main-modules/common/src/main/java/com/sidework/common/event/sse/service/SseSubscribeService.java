package com.sidework.common.event.sse.service;

import com.sidework.common.auth.CurrentUserProvider;
import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.common.event.sse.port.out.SseSubscribeOutPort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SseSubscribeService implements SseSubscribeUseCase {

	private final SseSubscribeOutPort sseSubscribeOutPort;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public SseEmitter subscribeUser() {
        return sseSubscribeOutPort.subscribeUser(currentUserProvider.authenticatedUser().getId());
    }

    @Override
    public SseEmitter subscribeChat(Long chatRoomId) {
        return sseSubscribeOutPort.subscribeChatRoom(chatRoomId);
    }
}
