package com.sidework.common.event.sse.component;

import com.sidework.common.event.sse.port.out.SseSendOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseSendAdapter implements SseSendOutPort {
    private final ChatRoomSseEmitter chatRoomSseEmitter;
    private final UserSseEmitter userSseEmitter;

    @Override
    public void sendToUser(Long userId, Object data) {
        userSseEmitter.sendTo(userId, data);
    }

    @Override
    public void sendToChatRoom(Long chatRoomId, Object data) {
        chatRoomSseEmitter.sendTo(chatRoomId, data);
    }
}
