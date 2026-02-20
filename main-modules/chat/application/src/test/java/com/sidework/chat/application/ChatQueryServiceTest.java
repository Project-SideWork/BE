package com.sidework.chat.application;

import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatMessagePage;
import com.sidework.chat.application.service.ChatQueryService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatQueryServiceTest {
    @Mock
    private ChatMessageOutPort chatMessageRepository;

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
