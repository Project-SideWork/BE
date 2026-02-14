package com.sidework.chat.persistence.adapter;

import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.persistence.entity.ChatRoomEntity;
import com.sidework.chat.persistence.mapper.ChatRoomMapper;
import com.sidework.chat.persistence.repository.ChatRoomJpaRepository;
import com.sidework.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomPersistenceAdapter implements ChatRoomOutPort {
    private final ChatRoomJpaRepository repo;
    private final ChatRoomMapper mapper;

    @Override
    public Long save(ChatRoom chatRoom) {
        ChatRoomEntity entity = mapper.toEntity(chatRoom);
        return mapper.toDomain(repo.save(entity)).getId();
    }
}
