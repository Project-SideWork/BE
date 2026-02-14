package com.sidework.chat.persistence.mapper;

import com.sidework.chat.persistence.entity.ChatUserEntity;
import com.sidework.domain.ChatUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatUserMapper {
    ChatUser toDomain(ChatUserEntity entity);
    ChatUserEntity toEntity(ChatUser domain);
}
