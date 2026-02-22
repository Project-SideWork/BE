package com.sidework.chat.persistence;

import com.sidework.chat.application.port.out.ChatMessagePage;
import com.sidework.chat.persistence.adapter.ChatMessagePersistenceAdapter;
import com.sidework.chat.persistence.entity.ChatMessageEntity;
import com.sidework.chat.persistence.mapper.ChatMessageMapper;
import com.sidework.chat.persistence.repository.ChatMessageJpaRepository;
import com.sidework.domain.ChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatMessagePersistenceAdapterTest {
    @Mock
    private ChatMessageJpaRepository repo;

    @Mock
    private ChatMessageMapper mapper;

    @InjectMocks
    private ChatMessagePersistenceAdapter adapter;

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        ChatMessage created = createValidDomain(null);
        ChatMessageEntity beforeSaved = createValidEntity(null);
        ChatMessage savedDomain = createValidDomain(1L);
        ChatMessageEntity saved = createValidEntity(1L);

        when(mapper.toEntity(created)).thenReturn(beforeSaved);
        when(repo.save(beforeSaved)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(savedDomain);

        Long id = adapter.save(created);

        assertEquals(1L, id);

        verify(mapper).toEntity(created);
        verify(repo).save(beforeSaved);
        verify(mapper).toDomain(saved);
    }

    @Test
    void findByChatRoomIdAndIdGreaterThan은_조회하고자_하는_크기와_조회한_데이터의_길이가_같을_때_커서_정보를_null로_반환한다() {
        Long chatRoomId = 1L;
        Instant cursorCreatedAt = Instant.now();
        Long cursorId = 1L;
        int size = 3;

        List<ChatMessageEntity> entities = List.of(
                createValidEntity(1L), createValidEntity(2L), createValidEntity(3L)
        );

        when(repo.pageBy(chatRoomId, cursorCreatedAt, cursorId, PageRequest.of(0, size + 1))).thenReturn(entities);
        when(mapper.toDomain(any(ChatMessageEntity.class)))
                .thenAnswer(invocation -> {
                    ChatMessageEntity e = invocation.getArgument(0);
                    return new ChatMessage(
                            e.getId(),
                            1L,
                            1L,
                            "테스트",
                            false,
                            LocalDateTime.ofInstant(e.getCreatedAt(), ZoneOffset.UTC)
                    );
                });
        ChatMessagePage page = adapter.findByChatRoomIdAndIdGreaterThan(chatRoomId, cursorCreatedAt, cursorId, size);


        assertEquals(size, page.items().size());
        assertFalse(page.hasNext());
        assertNull(page.nextCursorCreatedAt());
        assertNull(page.nextCursorId());

        verify(repo).pageBy(chatRoomId, cursorCreatedAt, cursorId, PageRequest.of(0, size + 1));
    }

    @Test
    void findByChatRoomIdAndIdGreaterThan은_조회하고자_하는_크기가_조회한_데이터의_길이보다_클_때_커서_정보를_null로_반환한다() {
        Long chatRoomId = 1L;
        Instant cursorCreatedAt = Instant.now();
        Long cursorId = 1L;
        int size = 3;

        List<ChatMessageEntity> entities = List.of(
                createValidEntity(1L)
        );

        when(repo.pageBy(chatRoomId, cursorCreatedAt, cursorId, PageRequest.of(0, size + 1))).thenReturn(entities);
        when(mapper.toDomain(any(ChatMessageEntity.class)))
                .thenAnswer(invocation -> {
                    ChatMessageEntity e = invocation.getArgument(0);
                    return new ChatMessage(
                            e.getId(),
                            1L,
                            1L,
                            "테스트",
                            false,
                            LocalDateTime.ofInstant(e.getCreatedAt(), ZoneOffset.UTC)
                    );
                });

        ChatMessagePage page = adapter.findByChatRoomIdAndIdGreaterThan(chatRoomId, cursorCreatedAt, cursorId, size);

        assertNotEquals(size, page.items().size());
        assertFalse(page.hasNext());
        assertNull(page.nextCursorCreatedAt());
        assertNull(page.nextCursorId());

        verify(repo).pageBy(chatRoomId, cursorCreatedAt, cursorId, PageRequest.of(0, size + 1));
    }

    @Test
    void findByChatRoomIdAndIdGreaterThan은_조회하고자_하는_크기가_조회한_데이터의_길이보다_작을_때_커서_정보를_반환한다() {
        Long chatRoomId = 1L;
        Instant cursorCreatedAt = Instant.now();
        Long cursorId = 1L;
        int size = 3;

        List<ChatMessageEntity> entities = List.of(
                createValidEntity(1L), createValidEntity(2L), createValidEntity(3L), createValidEntity(4L)
        );

        when(repo.pageBy(chatRoomId, cursorCreatedAt, cursorId, PageRequest.of(0, size + 1))).thenReturn(entities);
        when(mapper.toDomain(any(ChatMessageEntity.class)))
                .thenAnswer(invocation -> {
                    ChatMessageEntity e = invocation.getArgument(0);
                    return new ChatMessage(
                            e.getId(),
                            1L,
                            1L,
                            "테스트",
                            false,
                            LocalDateTime.ofInstant(e.getCreatedAt(), ZoneOffset.UTC)
                    );
                });

        ChatMessagePage page = adapter.findByChatRoomIdAndIdGreaterThan(chatRoomId, cursorCreatedAt, cursorId, size);

        assertEquals(size, page.items().size());
        assertTrue(page.hasNext());
        assertNotNull(page.nextCursorCreatedAt());
        assertEquals(page.items().getLast().getId(), page.nextCursorId());

        verify(repo).pageBy(chatRoomId, cursorCreatedAt, cursorId, PageRequest.of(0, size + 1));
    }

    private ChatMessage createValidDomain(Long id) {
        return new ChatMessage(id, 1L, 1L, "테스트", false, LocalDateTime.now());
    }

    private ChatMessageEntity createValidEntity(Long id) {
        ChatMessageEntity entity = new ChatMessageEntity(id, 1L, 1L, "테스트", false);
        ReflectionTestUtils.setField(entity, "createdAt", Instant.now());

        return entity;
    }


    private ChatMessagePage createPage() {
        return new ChatMessagePage(
                List.of(createValidDomain(1L), createValidDomain(2L), createValidDomain(3L)),
                true,
                LocalDateTime.now(),
                4L
        );
    }
}
