package com.sidework.chat.persistence.mapper;

import com.sidework.chat.persistence.entity.ChatMessageEntity;
import com.sidework.domain.ChatMessage;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ZoneId PROJECT_ZONE = ZoneId.of("Asia/Seoul");

    ChatMessage toDomain(ChatMessageEntity entity);
    ChatMessageEntity toEntity(ChatMessage domain);

    default Instant map(LocalDateTime value) {
        if (value == null) return null;
        return value
                .atZone(PROJECT_ZONE)
                .toInstant();
    }

    default LocalDateTime map(Instant value) {
        if (value == null) return null;
        return value
                .atZone(PROJECT_ZONE)
                .toLocalDateTime();
    }
}
