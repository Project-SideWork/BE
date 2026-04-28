package com.sidework.common.event.sse.port.out;

public record ChatMessageData(
        Long messageId, String content, String sendTime, Long senderUserId
) {
}
