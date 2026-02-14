package com.sidework.chat.persistence.mapper;

import com.sidework.chat.persistence.entity.ChatMessageEntity;
import com.sidework.domain.ChatMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ChatMessage toDomain(ChatMessageEntity entity);
    ChatMessageEntity toEntity(ChatMessage domain);
}
