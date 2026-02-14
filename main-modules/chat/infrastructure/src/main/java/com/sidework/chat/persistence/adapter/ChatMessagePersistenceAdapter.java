package com.sidework.chat.persistence.adapter;

import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.persistence.entity.ChatMessageEntity;
import com.sidework.chat.persistence.mapper.ChatMessageMapper;
import com.sidework.chat.persistence.repository.ChatMessageJpaRepository;
import com.sidework.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePersistenceAdapter implements ChatMessageOutPort {
    private final ChatMessageJpaRepository repo;
    private final ChatMessageMapper mapper;
    @Override
    public Long save(ChatMessage chatMessage) {
        ChatMessageEntity entity = mapper.toEntity(chatMessage);
        return mapper.toDomain(repo.save(entity)).getId();
    }
}
