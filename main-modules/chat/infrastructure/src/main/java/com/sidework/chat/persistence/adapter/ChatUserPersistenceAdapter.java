package com.sidework.chat.persistence.adapter;

import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.chat.persistence.entity.ChatUserEntity;
import com.sidework.chat.persistence.mapper.ChatUserMapper;
import com.sidework.chat.persistence.repository.ChatUserJpaRepository;
import com.sidework.domain.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatUserPersistenceAdapter implements ChatUserOutPort {
    private final ChatUserJpaRepository repo;
    private final ChatUserMapper mapper;
    @Override
    public void save(ChatUser chatUser) {
        ChatUserEntity entity = mapper.toEntity(chatUser);
        repo.save(entity);
    }
}
