package com.sidework.common.util;

import java.time.Instant;
import java.util.Base64;

public class CursorUtil {

    public static String encode(CursorWrapper cursor) {
        Instant createdAt = cursor.cursorCreatedAt();
        Long id = cursor.cursorId();

        if (createdAt == null || id == null) return null;

        String raw = createdAt.toEpochMilli() + ":" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes());
    }

    public static CursorWrapper decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return new CursorWrapper(null, null);
        }

        try {
            String raw = new String(Base64.getUrlDecoder().decode(cursor));
            String[] parts = raw.split(":");
            Instant instant = Instant.ofEpochMilli(Long.parseLong(parts[0]));
            Long id = Long.parseLong(parts[1]);
            return new CursorWrapper(instant, id);
        } catch (Exception e) {
            return new CursorWrapper(null, null);
        }
    }
}