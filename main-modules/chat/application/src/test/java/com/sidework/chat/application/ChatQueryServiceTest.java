package com.sidework.chat.application;

import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatMessagePage;
import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.application.port.out.ChatUserOutPort;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
    void queryMessagesByChatRoomId는_chatRoomId와_cursor로_메시지를_조회한다() {
        Instant now = Instant.now();
        Long cursorId = 10L;

        String encodedCursor =
                CursorUtil.encode(new CursorWrapper(now, cursorId));

        when(chatMessageRepository.findByChatRoomIdAndIdGreaterThan(
                anyLong(),
                any(),
                anyLong(),
                anyInt()
        )).thenReturn(createPage());

        ChatMessageQueryResult result =
                service.queryMessagesByChatRoomId(1L, encodedCursor);

        assertEquals(1, result.items().size());
        assertTrue(result.hasNext());
        assertNotNull(result.nextCursor());
    }

    @Test
    void queryMessagesByChatRoomId는_조회대상이_더_안남아있으면_cursor정보를_null로_반환한다() {
        Instant now = Instant.now();
        Long cursorId = 10L;

        String encodedCursor =
                CursorUtil.encode(new CursorWrapper(now, cursorId));

        when(chatMessageRepository.findByChatRoomIdAndIdGreaterThan(
                anyLong(),
                any(),
                anyLong(),
                anyInt()
        )).thenReturn(createLastPage());

        ChatMessageQueryResult result =
                service.queryMessagesByChatRoomId(1L, encodedCursor);

        assertEquals(1, result.items().size());
        assertFalse(result.hasNext());
        assertNull(result.nextCursor());
    }

    @Test
    void checkSubscribeValidation은_존재하지_않는_chatRoomId를_받으면_InvalidCommandException을_던진다() {
        Long chatRoomId = 1L;

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(false);

        assertThrows(
                InvalidCommandException.class,
                () -> service.checkChatUserValidation(1L, chatRoomId)
        );
    }

    @Test
    void checkSubscribeValidation은_existsByUserAndRoom가_거짓이면_ForbiddenAccessException을_던진다() {
        Long chatRoomId = 1L;
        Long userId = 1L;

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(chatUserRepository.existsByUserAndRoom(userId, chatRoomId)).thenReturn(false);

        assertThrows(
                ForbiddenAccessException.class,
                () -> service.checkChatUserValidation(userId, chatRoomId)
        );
    }

    @Test
    void checkSubscribeValidation은_existsById와_existsByUserAndRoom가_모두_참이면_예외를_던지지_않는다() {
        Long chatRoomId = 1L;
        Long userId = 1L;

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(chatUserRepository.existsByUserAndRoom(userId, chatRoomId)).thenReturn(true);

        assertDoesNotThrow(() ->
                service.checkChatUserValidation(userId, chatRoomId)
        );
    }

    private ChatMessagePage createPage() {
        ChatMessage message = new ChatMessage(
                1L,
                1L,
                1L,
                "hello",
                false,
                LocalDateTime.now()
        );

        return new ChatMessagePage(
                List.of(message),
                true,
                LocalDateTime.now(),
                1L
        );
    }

    private ChatMessagePage createLastPage() {
        ChatMessage message = new ChatMessage(
                1L,
                1L,
                1L,
                "hello",
                false,
                LocalDateTime.now()
        );

        return new ChatMessagePage(
                List.of(message),
                false,
                LocalDateTime.now(),
                null
        );
    }
}
