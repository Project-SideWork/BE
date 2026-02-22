package com.sidework.common.response;

import java.util.List;

public record CursorResponse<T>(
        List<T> content,
        String nextCursor,
        boolean hasNext
) {}