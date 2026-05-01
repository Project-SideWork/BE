package com.sidework.chat.application.port.in;

import java.util.List;

public record ChatMessageQueryResult(
        List<ChatRecord> items,
        String nextCursor,
        boolean hasNext
) {
}
