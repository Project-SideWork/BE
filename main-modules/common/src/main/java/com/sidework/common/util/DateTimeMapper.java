package com.sidework.common.util;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface DateTimeMapper {
    ZoneId ZONE = ZoneId.of("Asia/Seoul");

    default Instant toInstant(LocalDate value) {
        if (value == null) return null;
        return value.atStartOfDay(ZONE).toInstant();
    }

    default LocalDate toLocalDate(Instant value) {
        if (value == null) return null;
        return value.atZone(ZONE).toLocalDate();
    }

    default Instant toInstant(LocalDateTime value) {
        if (value == null) return null;
        return value.atZone(ZONE).toInstant();
    }

    default LocalDateTime toLocalDateTime(Instant value) {
        if (value == null) return null;
        return value.atZone(ZONE).toLocalDateTime();
    }
}
