package com.sidework.common.event;

import com.sidework.common.event.sse.port.out.ChatMessageData;

public record ChatRoomSseSendEvent(
        Long chatRoomId,
        ChatMessageData data
) implements SseSendEvent {

}