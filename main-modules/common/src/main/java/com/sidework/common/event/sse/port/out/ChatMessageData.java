package com.sidework.common.event.sse.port.out;

import java.time.Instant;

public record ChatMessageData(
        Long messageId, String content, Instant sendTime, Long senderUserId
) {
}
