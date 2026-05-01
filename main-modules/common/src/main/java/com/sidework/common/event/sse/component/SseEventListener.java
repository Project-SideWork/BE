package com.sidework.common.event.sse.component;

import com.sidework.common.event.ChatRoomSseSendEvent;
import com.sidework.common.event.UserSseSendEvent;
import com.sidework.common.event.sse.port.out.SseSendOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component

@RequiredArgsConstructor

public class SseEventListener {

    private final SseSendOutPort sseSendOutPort;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleUserSse(UserSseSendEvent event) {
        sseSendOutPort.sendToUser(event.userId(), event.data());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleChatRoomSse(ChatRoomSseSendEvent event) {
        sseSendOutPort.sendToChatRoom(event.chatRoomId(), event.data());
    }
}