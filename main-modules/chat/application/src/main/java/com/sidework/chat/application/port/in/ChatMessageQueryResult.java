package com.sidework.chat.application.port.in;

import com.sidework.chat.application.adapter.ChatRecord;

import java.util.List;

public record ChatMessageQueryResult(
        List<ChatRecord> items,
        String nextCursor,
        Boolean hasNext
) {
}
