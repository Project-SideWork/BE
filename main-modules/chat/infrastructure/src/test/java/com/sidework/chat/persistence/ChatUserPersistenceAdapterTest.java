package com.sidework.chat.persistence;

import com.sidework.chat.application.port.out.ChatUserSummary;
import com.sidework.chat.application.port.out.ChatUserSummaryPage;
import com.sidework.chat.persistence.adapter.ChatUserPersistenceAdapter;
import com.sidework.chat.persistence.entity.ChatUserEntity;
import com.sidework.chat.persistence.mapper.ChatUserMapper;
import com.sidework.chat.persistence.repository.ChatUserJpaRepository;
import com.sidework.domain.ChatUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatUserPersistenceAdapterTest {

    @Mock
    private ChatUserJpaRepository repo;

    @Mock
    private ChatUserMapper mapper;

    @InjectMocks
    private ChatUserPersistenceAdapter adapter;

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        ChatUser chatUser = createValid();
        ChatUserEntity beforeSave = createValidBeforeSaved();

        when(mapper.toEntity(chatUser)).thenReturn(beforeSave);
        when(repo.save(beforeSave)).thenReturn(createValidSaved());

        adapter.save(chatUser);

        verify(mapper).toEntity(chatUser);
        verify(repo).save(beforeSave);
    }

    @Test
    void updateLastReadChat는_Repository_메서드를_호출하고_결과를_반환한다() {
        when(repo.updateLastRead(1L, 1L, 2L)).thenReturn(1);

        int result = adapter.updateLastReadChat(1L, 1L, 2L);

        assertEquals(1, result);
        verify(repo).updateLastRead(1L, 1L, 2L);
    }

    @Test
    void updateLastReadChat는_업데이트_대상이_없으면_0을_반환한다() {
        when(repo.updateLastRead(1L, 999L, 2L)).thenReturn(0);

        int result = adapter.updateLastReadChat(1L, 999L, 2L);

        assertEquals(0, result);
        verify(repo).updateLastRead(1L, 999L, 2L);
    }

    @Test
    void updateIsConnected는_Repository_메서드를_호출한다() {
        adapter.updateIsConnected(1L, 1L, true);

        verify(repo).updateIsConnected(1L, 1L, true);
    }

    @Test
    void existsByUserAndRoom은_repository_결과가_true이면_true를_반환한다() {
        when(repo.existsByUserAndChatRoom(1L, 1L)).thenReturn(true);

        boolean result = adapter.existsByUserAndRoom(1L, 1L);

        assertTrue(result);
        verify(repo).existsByUserAndChatRoom(1L, 1L);
    }

    @Test
    void existsByUserAndRoom은_repository_결과가_false이면_false를_반환한다() {
        when(repo.existsByUserAndChatRoom(1L, 1L)).thenReturn(false);

        boolean result = adapter.existsByUserAndRoom(1L, 1L);

        assertFalse(result);
        verify(repo).existsByUserAndChatRoom(1L, 1L);
    }

    @Test
    void isChatRoomConnected는_repository_결과가_true이면_true를_반환한다() {
        when(repo.findConnectedByUserAndChatRoom(1L, 1L)).thenReturn(true);

        boolean result = adapter.isChatRoomConnected(1L, 1L);

        assertTrue(result);
        verify(repo).findConnectedByUserAndChatRoom(1L, 1L);
    }

    @Test
    void isChatRoomConnected는_repository_결과가_false이면_false를_반환한다() {
        when(repo.findConnectedByUserAndChatRoom(1L, 1L)).thenReturn(false);

        boolean result = adapter.isChatRoomConnected(1L, 1L);

        assertFalse(result);
        verify(repo).findConnectedByUserAndChatRoom(1L, 1L);
    }

    @Test
    void findChatPairInRoom은_상대방_userId를_반환한다() {
        when(repo.findChatPair(1L, 10L)).thenReturn(2L);

        Long result = adapter.findChatPairInRoom(1L, 10L);

        assertEquals(2L, result);
        verify(repo).findChatPair(1L, 10L);
    }

    @Test
    void findByUserIdAndIdGreaterThan은_채팅방_목록을_조회하고_다음_페이지가_있으면_hasNext_true를_반환한다() {
        Instant cursorCreatedAt = Instant.parse("2026-04-29T07:00:00Z");
        Long cursorId = 10L;
        int size = 3;

        List<ChatUserSummary> summaries = List.of(
                createSummary(
                        1L,
                        "메시지1",
                        LocalDateTime.parse("2026-04-29T06:00:00"),
                        0L,
                        LocalDateTime.parse("2026-04-29T06:00:00")
                ),
                createSummary(
                        2L,
                        "메시지2",
                        LocalDateTime.parse("2026-04-29T05:00:00"),
                        1L,
                        LocalDateTime.parse("2026-04-29T05:00:00")
                ),
                createSummary(
                        3L,
                        "메시지3",
                        LocalDateTime.parse("2026-04-29T04:00:00"),
                        2L,
                        LocalDateTime.parse("2026-04-29T04:00:00")
                ),
                createSummary(
                        4L,
                        "메시지4",
                        LocalDateTime.parse("2026-04-29T03:00:00"),
                        3L,
                        LocalDateTime.parse("2026-04-29T03:00:00")
                )
        );

        when(repo.findChatUserSummary(
                1L,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        )).thenReturn(summaries);

        ChatUserSummaryPage result =
                adapter.findByUserIdAndIdGreaterThan(1L, cursorCreatedAt, cursorId, size);

        assertEquals(3, result.items().size());
        assertTrue(result.hasNext());
        assertEquals(3L, result.nextCursorId());
        assertEquals(
                LocalDateTime.ofInstant(Instant.parse("2026-04-29T04:00:00Z"), java.time.ZoneOffset.UTC),
                result.nextCursorCreatedAt()
        );

        verify(repo).findChatUserSummary(
                1L,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        );
    }

    @Test
    void findByUserIdAndIdGreaterThan은_다음_페이지가_없으면_hasNext_false를_반환한다() {
        Long userId = 1L;
        int size = 3;

        List<ChatUserSummary> summaries = List.of(
                new ChatUserSummary(1L, "메시지1", LocalDateTime.now(), 0L, LocalDateTime.now()),
                new ChatUserSummary(2L, "메시지2", LocalDateTime.now(), 1L, LocalDateTime.now()),
                new ChatUserSummary(3L, "메시지3", LocalDateTime.now(), 2L, LocalDateTime.now())
        );

        when(repo.findChatUserSummary(
                eq(userId),
                isNull(),
                isNull(),
                any(PageRequest.class)
        )).thenReturn(summaries);

        ChatUserSummaryPage result =
                adapter.findByUserIdAndIdGreaterThan(userId, null, null, size);

        assertEquals(3, result.items().size());
        assertFalse(result.hasNext());
        assertNull(result.nextCursorCreatedAt());
        assertNull(result.nextCursorId());
    }

    @Test
    void findByUserIdAndIdGreaterThan은_조회된_채팅방이_없으면_cursor정보를_null로_반환한다() {
        Instant cursorCreatedAt = Instant.parse("2026-04-29T07:00:00Z");
        Long cursorId = 10L;
        int size = 3;

        when(repo.findChatUserSummary(
                1L,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        )).thenReturn(List.of());

        ChatUserSummaryPage result =
                adapter.findByUserIdAndIdGreaterThan(1L, cursorCreatedAt, cursorId, size);

        assertTrue(result.items().isEmpty());
        assertFalse(result.hasNext());
        assertNull(result.nextCursorCreatedAt());
        assertNull(result.nextCursorId());

        verify(repo).findChatUserSummary(
                1L,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, size + 1)
        );
    }

    private ChatUser createValid() {
        return new ChatUser(null, 1L, 1L, null, false);
    }

    private ChatUserEntity createValidBeforeSaved() {
        return new ChatUserEntity(null, 1L, 1L, null, false);
    }

    private ChatUserEntity createValidSaved() {
        return new ChatUserEntity(1L, 1L, 1L, null, false);
    }

    private ChatUserSummary createSummary(
            Long chatRoomId,
            String lastMessageContent,
            LocalDateTime lastMessageSendTime,
            Long unreadCount,
            LocalDateTime createdAt
    ) {
        return new ChatUserSummary(
                chatRoomId,
                lastMessageContent,
                lastMessageSendTime,
                unreadCount,
                createdAt
        );
    }
}