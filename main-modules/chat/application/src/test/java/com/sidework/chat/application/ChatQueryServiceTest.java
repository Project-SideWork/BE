package com.sidework.chat.application;

import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.in.ChatRoomQueryResult;
import com.sidework.chat.application.port.out.*;
import com.sidework.chat.application.service.ChatQueryService;
import com.sidework.common.exception.ForbiddenAccessException;
import com.sidework.common.exception.InvalidCommandException;
import com.sidework.common.util.CursorUtil;
import com.sidework.common.util.CursorWrapper;
import com.sidework.domain.ChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatQueryServiceTest {

    @Mock
    private ChatMessageOutPort chatMessageRepository;

    @Mock
    private ChatRoomOutPort chatRoomRepository;

    @Mock
    private ChatUserOutPort chatUserRepository;

    @InjectMocks
    private ChatQueryService service;

    @Test
    void queryMessagesByChatRoomId는_chatRoomId와_cursor로_메시지를_조회하고_lastReadChatId를_업데이트한다() {
        Instant now = Instant.now();
        Long cursorId = 10L;

        String encodedCursor = CursorUtil.encode(new CursorWrapper(now, cursorId));

        when(chatMessageRepository.findByChatRoomIdAndIdGreaterThan(
                anyLong(),
                any(),
                anyLong(),
                anyInt()
        )).thenReturn(createMessagePage());

        when(chatUserRepository.updateLastReadChat(1L, 1L, 1L))
                .thenReturn(1);

        ChatMessageQueryResult result =
                service.queryMessagesByChatRoomId(1L, 1L, encodedCursor);

        assertEquals(1, result.items().size());
        assertTrue(result.hasNext());
        assertNotNull(result.nextCursor());

        verify(chatMessageRepository).findByChatRoomIdAndIdGreaterThan(
                eq(1L),
                any(),
                eq(10L),
                eq(3)
        );

        verify(chatUserRepository).updateLastReadChat(1L, 1L, 1L);
    }

    @Test
    void queryMessagesByChatRoomId는_cursor가_null이어도_메시지를_조회한다() {
        when(chatMessageRepository.findByChatRoomIdAndIdGreaterThan(
                anyLong(),
                any(),
                any(),
                anyInt()
        )).thenReturn(createMessagePage());

        when(chatUserRepository.updateLastReadChat(1L, 1L, 1L))
                .thenReturn(1);

        ChatMessageQueryResult result =
                service.queryMessagesByChatRoomId(1L, 1L, null);

        assertEquals(1, result.items().size());
        assertTrue(result.hasNext());

        verify(chatMessageRepository).findByChatRoomIdAndIdGreaterThan(
                eq(1L),
                any(),
                any(),
                eq(3)
        );

        verify(chatUserRepository).updateLastReadChat(1L, 1L, 1L);
    }

    @Test
    void queryMessagesByChatRoomId는_조회대상이_더_안남아있으면_hasNext_false를_반환한다() {
        Instant now = Instant.now();
        Long cursorId = 10L;

        String encodedCursor = CursorUtil.encode(new CursorWrapper(now, cursorId));

        when(chatMessageRepository.findByChatRoomIdAndIdGreaterThan(
                anyLong(),
                any(),
                anyLong(),
                anyInt()
        )).thenReturn(createLastMessagePage());

        when(chatUserRepository.updateLastReadChat(1L, 1L, 1L))
                .thenReturn(1);

        ChatMessageQueryResult result =
                service.queryMessagesByChatRoomId(1L, 1L, encodedCursor);

        assertEquals(1, result.items().size());
        assertFalse(result.hasNext());

        verify(chatUserRepository).updateLastReadChat(1L, 1L, 1L);
    }

    @Test
    void queryRoomsByUserId는_userId와_cursor로_채팅방목록을_조회한다() {
        Instant now = Instant.now();
        Long cursorId = 10L;

        String encodedCursor = CursorUtil.encode(new CursorWrapper(now, cursorId));

        when(chatUserRepository.findByUserIdAndIdGreaterThan(
                anyLong(),
                any(),
                anyLong(),
                anyInt()
        )).thenReturn(createChatUserSummaryPage());

        ChatRoomQueryResult result =
                service.queryRoomsByUserId(1L, encodedCursor);

        assertEquals(1, result.items().size());
        assertTrue(result.hasNext());
        assertNotNull(result.nextCursor());

        verify(chatUserRepository).findByUserIdAndIdGreaterThan(
                eq(1L),
                any(),
                eq(10L),
                eq(3)
        );
    }

    @Test
    void queryRoomsByUserId는_cursor가_null이어도_채팅방목록을_조회한다() {
        when(chatUserRepository.findByUserIdAndIdGreaterThan(
                anyLong(),
                any(),
                any(),
                anyInt()
        )).thenReturn(createChatUserSummaryPage());

        ChatRoomQueryResult result =
                service.queryRoomsByUserId(1L, null);

        assertEquals(1, result.items().size());
        assertTrue(result.hasNext());

        verify(chatUserRepository).findByUserIdAndIdGreaterThan(
                eq(1L),
                any(),
                any(),
                eq(3)
        );
    }

    @Test
    void queryRoomsByUserId는_조회대상이_더_안남아있으면_hasNext_false를_반환한다() {
        Instant now = Instant.now();
        Long cursorId = 10L;

        String encodedCursor = CursorUtil.encode(new CursorWrapper(now, cursorId));

        when(chatUserRepository.findByUserIdAndIdGreaterThan(
                anyLong(),
                any(),
                anyLong(),
                anyInt()
        )).thenReturn(createLastChatUserSummaryPage());

        ChatRoomQueryResult result =
                service.queryRoomsByUserId(1L, encodedCursor);

        assertEquals(1, result.items().size());
        assertFalse(result.hasNext());

        verify(chatUserRepository).findByUserIdAndIdGreaterThan(
                eq(1L),
                any(),
                eq(10L),
                eq(3)
        );
    }

    @Test
    void checkChatUserValidation은_존재하지_않는_chatRoomId를_받으면_InvalidCommandException을_던진다() {
        Long chatRoomId = 1L;

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(false);

        assertThrows(
                InvalidCommandException.class,
                () -> service.checkChatUserValidation(1L, chatRoomId)
        );

        verify(chatRoomRepository).existsById(chatRoomId);
        verify(chatUserRepository, never()).existsByUserAndRoom(anyLong(), anyLong());
    }

    @Test
    void checkChatUserValidation은_채팅방은_존재하지만_유저가_속하지_않으면_ForbiddenAccessException을_던진다() {
        Long chatRoomId = 1L;
        Long userId = 1L;

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(chatUserRepository.existsByUserAndRoom(userId, chatRoomId)).thenReturn(false);

        assertThrows(
                ForbiddenAccessException.class,
                () -> service.checkChatUserValidation(userId, chatRoomId)
        );

        verify(chatRoomRepository).existsById(chatRoomId);
        verify(chatUserRepository).existsByUserAndRoom(userId, chatRoomId);
    }

    @Test
    void checkChatUserValidation은_채팅방이_존재하고_유저가_속해있으면_예외를_던지지_않는다() {
        Long chatRoomId = 1L;
        Long userId = 1L;

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(chatUserRepository.existsByUserAndRoom(userId, chatRoomId)).thenReturn(true);

        assertDoesNotThrow(() ->
                service.checkChatUserValidation(userId, chatRoomId)
        );

        verify(chatRoomRepository).existsById(chatRoomId);
        verify(chatUserRepository).existsByUserAndRoom(userId, chatRoomId);
    }

    private ChatMessagePage createMessagePage() {
        ChatMessage message = new ChatMessage(
                1L,
                1L,
                1L,
                "hello",
                LocalDateTime.now(),
                false
        );

        return new ChatMessagePage(
                List.of(message),
                true,
                LocalDateTime.now(),
                1L
        );
    }

    private ChatMessagePage createLastMessagePage() {
        ChatMessage message = new ChatMessage(
                1L,
                1L,
                1L,
                "hello",
                LocalDateTime.now(),
                false
        );

        return new ChatMessagePage(
                List.of(message),
                false,
                null,
                null
        );
    }

    private ChatUserSummaryPage createChatUserSummaryPage() {
        ChatUserSummary summary = new ChatUserSummary(
                1L,
                "마지막 메시지",
                Instant.now(),
                3L,
                Instant.now()
        );

        return new ChatUserSummaryPage(
                List.of(summary),
                true,
                LocalDateTime.now(),
                1L
        );
    }

    private ChatUserSummaryPage createLastChatUserSummaryPage() {
        ChatUserSummary summary = new ChatUserSummary(
                1L,
                "마지막 메시지",
                Instant.now(),
                0L,
                Instant.now()
        );

        return new ChatUserSummaryPage(
                List.of(summary),
                false,
                null,
                null
        );
    }
}