package com.sidework.chat.application.port.in;

import java.util.List;

public record ChatRoomQueryResult(
        List<ChatRoomRecord> items,
        String nextCursor,
        boolean hasNext
) {
}
