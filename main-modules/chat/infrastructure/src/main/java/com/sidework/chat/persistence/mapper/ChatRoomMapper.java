package com.sidework.chat.persistence.mapper;

import com.sidework.chat.persistence.entity.ChatRoomEntity;
import com.sidework.domain.ChatRoom;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {
    ZoneId PROJECT_ZONE = ZoneId.of("Asia/Seoul");

    ChatRoom toDomain(ChatRoomEntity entity);
    ChatRoomEntity toEntity(ChatRoom domain);

    default Instant map(LocalDateTime value) {
        if (value == null) return null;
        return value
                .atZone(PROJECT_ZONE)
                .toInstant();
    }

    default LocalDate map(Instant value) {
        if (value == null) return null;
        return value
                .atZone(PROJECT_ZONE)
                .toLocalDate();
    }
}
