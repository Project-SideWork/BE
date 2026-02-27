package com.sidework.common.response;

import org.springframework.data.domain.Pageable;

public record PageResponse<T>(
        T content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageResponse<T> of(T content, int page, int size, long totalElements, int totalPages) {
        return new PageResponse<>(content, page + 1, size, totalElements, totalPages);
    }

    public static <T> PageResponse<T> empty(Pageable pageable) {
        return new PageResponse<>(null, pageable.getPageNumber() + 1, pageable.getPageSize(), 0, 0);
    }
}
