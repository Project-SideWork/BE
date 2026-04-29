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
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomPersistenceAdapterTest {

    @Mock
    private ChatRoomJpaRepository repo;

    @Mock
    private ChatRoomMapper mapper;

    @InjectMocks
    private ChatRoomPersistenceAdapter adapter;

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장하고_ID를_반환한다() {
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
        verify(mapper).toDomain(saved);
    }

    @Test
    void existsById는_repository의_결과가_true이면_true를_반환한다() {
        when(repo.existsById(1L)).thenReturn(true);

        boolean result = adapter.existsById(1L);

        assertTrue(result);
        verify(repo).existsById(1L);
    }

    @Test
    void existsById는_repository의_결과가_false이면_false를_반환한다() {
        when(repo.existsById(1L)).thenReturn(false);

        boolean result = adapter.existsById(1L);

        assertFalse(result);
        verify(repo).existsById(1L);
    }

    @Test
    void updateChatRoomLatest는_마지막_메시지_정보를_업데이트하고_영향받은_row수를_반환한다() {
        LocalDateTime messageSendTime = LocalDateTime.of(2026, 4, 29, 16, 30);
        Instant expectedInstant = messageSendTime
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant();

        when(repo.updateLastMessage(
                "테스트 메시지",
                expectedInstant,
                10L,
                3L,
                1L
        )).thenReturn(1);

        int result = adapter.updateChatRoomLatest(
                "테스트 메시지",
                messageSendTime,
                10L,
                3L,
                1L
        );

        assertEquals(1, result);

        verify(repo).updateLastMessage(
                "테스트 메시지",
                expectedInstant,
                10L,
                3L,
                1L
        );
    }

    @Test
    void updateChatRoomLatest는_업데이트_대상이_없으면_0을_반환한다() {
        LocalDateTime messageSendTime = LocalDateTime.of(2026, 4, 29, 16, 30);
        Instant expectedInstant = messageSendTime
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant();

        when(repo.updateLastMessage(
                "테스트 메시지",
                expectedInstant,
                10L,
                3L,
                999L
        )).thenReturn(0);

        int result = adapter.updateChatRoomLatest(
                "테스트 메시지",
                messageSendTime,
                10L,
                3L,
                999L
        );

        assertEquals(0, result);

        verify(repo).updateLastMessage(
                "테스트 메시지",
                expectedInstant,
                10L,
                3L,
                999L
        );
    }

    private ChatRoom createValidDomain() {
        return new ChatRoom(null, "테스트", LocalDateTime.now(), 1L, 1L);
    }

    private ChatRoom createDomainValidSaved() {
        return new ChatRoom(1L, "테스트", LocalDateTime.now(),1L, 1L);
    }

    private ChatRoomEntity createValidBeforeSaved() {
        return new ChatRoomEntity(null, "테스트", Instant.now(), 1L, 1L);
    }

    private ChatRoomEntity createValidSaved() {
        return new ChatRoomEntity(1L, "테스트", Instant.now(), 1L, 1L);
    }
}