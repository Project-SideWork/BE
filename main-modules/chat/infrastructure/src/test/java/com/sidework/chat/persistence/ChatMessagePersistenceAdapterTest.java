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
        ChatMessageEntity beforeSaved = createValidEntity(null, Instant.parse("2026-04-29T00:00:00Z"));
        ChatMessageEntity saved = createValidEntity(1L, Instant.parse("2026-04-29T00:00:01Z"));
        ChatMessage savedDomain = createValidDomain(1L);

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
    void findByChatRoomIdAndIdGreaterThan은_조회결과가_size와_같으면_hasNext_false와_cursor_null을_반환한다() {
        Long chatRoomId = 1L;
        Instant cursorCreatedAt = Instant.parse("2026-04-29T00:00:00Z");
        Long cursorId = 1L;
        int size = 3;

        List<ChatMessageEntity> entities = List.of(
                createValidEntity(1L, Instant.parse("2026-04-29T00:00:01Z")),
                createValidEntity(2L, Instant.parse("2026-04-29T00:00:02Z")),
                createValidEntity(3L, Instant.parse("2026-04-29T00:00:03Z"))
        );

        when(repo.pageBy(
                chatRoomId,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        )).thenReturn(entities);

        when(mapper.toDomain(any(ChatMessageEntity.class)))
                .thenAnswer(invocation -> {
                    ChatMessageEntity entity = invocation.getArgument(0);
                    return createDomainFromEntity(entity);
                });

        ChatMessagePage page =
                adapter.findByChatRoomIdAndIdGreaterThan(chatRoomId, cursorCreatedAt, cursorId, size);

        assertEquals(size, page.items().size());
        assertFalse(page.hasNext());
        assertNull(page.nextCursorCreatedAt());
        assertNull(page.nextCursorId());

        verify(repo).pageBy(
                chatRoomId,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        );
    }

    @Test
    void findByChatRoomIdAndIdGreaterThan은_조회결과가_size보다_작으면_hasNext_false와_cursor_null을_반환한다() {
        Long chatRoomId = 1L;
        Instant cursorCreatedAt = Instant.parse("2026-04-29T00:00:00Z");
        Long cursorId = 1L;
        int size = 3;

        List<ChatMessageEntity> entities = List.of(
                createValidEntity(1L, Instant.parse("2026-04-29T00:00:01Z"))
        );

        when(repo.pageBy(
                chatRoomId,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        )).thenReturn(entities);

        when(mapper.toDomain(any(ChatMessageEntity.class)))
                .thenAnswer(invocation -> {
                    ChatMessageEntity entity = invocation.getArgument(0);
                    return createDomainFromEntity(entity);
                });

        ChatMessagePage page =
                adapter.findByChatRoomIdAndIdGreaterThan(chatRoomId, cursorCreatedAt, cursorId, size);

        assertEquals(1, page.items().size());
        assertFalse(page.hasNext());
        assertNull(page.nextCursorCreatedAt());
        assertNull(page.nextCursorId());

        verify(repo).pageBy(
                chatRoomId,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        );
    }

    @Test
    void findByChatRoomIdAndIdGreaterThan은_size보다_1개_더_조회되면_hasNext_true와_다음커서를_반환한다() {
        Long chatRoomId = 1L;
        Instant cursorCreatedAt = Instant.parse("2026-04-29T00:00:00Z");
        Long cursorId = 1L;
        int size = 3;

        Instant t1 = Instant.parse("2026-04-29T00:00:01Z");
        Instant t2 = Instant.parse("2026-04-29T00:00:02Z");
        Instant t3 = Instant.parse("2026-04-29T00:00:03Z");
        Instant t4 = Instant.parse("2026-04-29T00:00:04Z");

        List<ChatMessageEntity> entities = List.of(
                createValidEntity(1L, t1),
                createValidEntity(2L, t2),
                createValidEntity(3L, t3),
                createValidEntity(4L, t4)
        );

        when(repo.pageBy(
                chatRoomId,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        )).thenReturn(entities);

        when(mapper.toDomain(any(ChatMessageEntity.class)))
                .thenAnswer(invocation -> {
                    ChatMessageEntity entity = invocation.getArgument(0);
                    return createDomainFromEntity(entity);
                });

        ChatMessagePage page =
                adapter.findByChatRoomIdAndIdGreaterThan(chatRoomId, cursorCreatedAt, cursorId, size);

        assertEquals(size, page.items().size());
        assertTrue(page.hasNext());

        assertEquals(
                LocalDateTime.ofInstant(t3, ZoneOffset.UTC),
                page.nextCursorCreatedAt()
        );
        assertEquals(3L, page.nextCursorId());

        verify(repo).pageBy(
                chatRoomId,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        );
    }

    private ChatMessage createValidDomain(Long id) {
        return new ChatMessage(
                id,
                1L,
                1L,
                "테스트",
                LocalDateTime.now(),
                true
        );
    }

    private ChatMessage createDomainFromEntity(ChatMessageEntity entity) {
        return new ChatMessage(
                entity.getId(),
                entity.getChatRoomId(),
                entity.getSenderId(),
                entity.getContent(),
                LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneOffset.UTC),
                entity.getIsDeleted()
        );
    }

    private ChatMessageEntity createValidEntity(Long id, Instant createdAt) {
        ChatMessageEntity entity = new ChatMessageEntity(
                id,
                1L,
                1L,
                "테스트",
                Instant.now(),
                false
        );

        ReflectionTestUtils.setField(entity, "createdAt", createdAt);

        return entity;
    }
}