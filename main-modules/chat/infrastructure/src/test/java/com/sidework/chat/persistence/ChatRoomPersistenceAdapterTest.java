package com.sidework.chat.persistence;

import com.sidework.chat.persistence.adapter.ChatRoomPersistenceAdapter;
import com.sidework.chat.persistence.entity.ChatRoomEntity;
import com.sidework.chat.persistence.mapper.ChatRoomMapper;
import com.sidework.chat.persistence.repository.ChatRoomJpaRepository;
import com.sidework.domain.ChatRoom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatRoomPersistenceAdapterTest {
    @Mock
    private ChatRoomJpaRepository repo;

    @Mock
    private ChatRoomMapper mapper;

    @InjectMocks
    private ChatRoomPersistenceAdapter adapter;

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        ChatRoom chatRoom = createValidDomain();
        ChatRoomEntity beforeSave = createValidBeforeSaved();
        ChatRoomEntity saved = createValidSaved();
        ChatRoom savedDomain = createDomainValidSaved();

        when(mapper.toEntity(chatRoom)).thenReturn(beforeSave);
        when(repo.save(beforeSave)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(savedDomain);

        Long id = adapter.save(chatRoom);

        assertEquals(1L, id);

        verify(mapper).toEntity(chatRoom);
        verify(repo).save(beforeSave);
    }

    private ChatRoom createValidDomain() {
        return new ChatRoom(null, "테스트", LocalDateTime.now());
    }


    private ChatRoom createDomainValidSaved() {
        return new ChatRoom(1L, "테스트", LocalDateTime.now());
    }

    private ChatRoomEntity createValidBeforeSaved() {
        return new ChatRoomEntity(null, "테스트", Instant.now());
    }

    private ChatRoomEntity createValidSaved() {
        return new ChatRoomEntity(1L, "테스트", Instant.now());
    }
}
