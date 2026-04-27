package com.sidework.chat.application.port.in;

import com.sidework.chat.application.adapter.ChatRoomRecord;

import java.util.List;

public record ChatRoomQueryResult(
        List<ChatRoomRecord> items,
        String nextCursor,
        Boolean hasNext
) {
}
