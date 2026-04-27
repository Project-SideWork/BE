package com.sidework.chat.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

public record ChatUserSummaryPage(
        List<ChatUserSummary> items,
        Boolean hasNext,
        LocalDateTime nextCursorCreatedAt,
        Long nextCursorId
) {
}
