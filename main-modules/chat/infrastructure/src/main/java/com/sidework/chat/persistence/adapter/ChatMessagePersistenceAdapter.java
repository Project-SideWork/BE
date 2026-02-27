package com.sidework.chat.persistence.adapter;

import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatMessagePage;
import com.sidework.chat.persistence.entity.ChatMessageEntity;
import com.sidework.chat.persistence.mapper.ChatMessageMapper;
import com.sidework.chat.persistence.repository.ChatMessageJpaRepository;
import com.sidework.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

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

    @Override
    public ChatMessagePage findByChatRoomIdAndIdGreaterThan(
            Long chatRoomId,
            Instant cursorCreatedAt,
            Long cursorId,
            int size) {

        List<ChatMessageEntity> entities = repo.pageBy(
                chatRoomId,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        );

        boolean hasNext = entities.size() > size;

        List<ChatMessageEntity> items = entities.stream()
                .limit(size)
                .toList();

        ChatMessageEntity last = hasNext && !items.isEmpty()
                ? items.getLast()
                : null;

        return new ChatMessagePage(
                items.stream().map(mapper::toDomain).toList(),
                hasNext,
                last != null
                        ? LocalDateTime.ofInstant(last.getCreatedAt(), ZoneOffset.UTC)
                        : null,
                last != null ? last.getId() : null
        );
    }
}
