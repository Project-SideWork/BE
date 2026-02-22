package com.sidework.common.util;

import java.time.Instant;

public record CursorWrapper(Instant cursorCreatedAt, Long cursorId) {
}