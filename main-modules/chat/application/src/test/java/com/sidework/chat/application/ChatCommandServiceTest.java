package com.sidework.chat.application;

import com.sidework.chat.application.adapter.NewChatCommand;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.chat.application.service.ChatCommandService;
import com.sidework.common.auth.CurrentUserProvider;
import com.sidework.common.event.sse.port.out.SseSendOutPort;
import com.sidework.domain.ChatMessage;
import com.sidework.domain.ChatRoom;
import com.sidework.domain.ChatUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatCommandServiceTest {
    @Mock
    private ChatMessageOutPort chatMessageRepository;

    @Mock
    private ChatUserOutPort chatUserRepository;

    @Mock
    private ChatRoomOutPort chatRoomRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private SseSendOutPort sseSendAdapter;

    @InjectMocks
    private ChatCommandService service;

    @Captor
    ArgumentCaptor<ChatUser> chatUserArgumentCaptor;

    @Test
    void createNewChatRoom은_ChatRoom을_저장_후_ID를_반환한다() {
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(1L);

        Long chatRoomId = service.createNewChatRoom("테스트");

        assertEquals(1L, chatRoomId);
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    void createNewChatMessage은_ChatMessage를_저장_후_ID를_반환한다() {
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);

        Long chatMessageId = service.createNewChatMessage(1L, 1L, "테스트");

        assertEquals(1L, chatMessageId);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void createNewChatUser은_메시지를_받은_ChatUser는_lastReadChatId를_null로_저장한다() {
        service.createNewChatUser(1L, 1L, null);

        verify(chatUserRepository).save(chatUserArgumentCaptor.capture());

        ChatUser saved = chatUserArgumentCaptor.getValue();
        assertEquals(1L, saved.getChatRoomId());
        assertEquals(1L, saved.getUserId());
        assertNull(saved.getLastReadChatId());
    }

    @Test
    void createNewChatUser은_메시지를_보낸_ChatUser는_lastReadChatId를_ChatMessageId로_저장한다() {
        service.createNewChatUser(1L, 1L, 1L);

        verify(chatUserRepository).save(chatUserArgumentCaptor.capture());

        ChatUser saved = chatUserArgumentCaptor.getValue();
        assertEquals(1L, saved.getChatRoomId());
        assertEquals(1L, saved.getUserId());
        assertEquals(1L, saved.getLastReadChatId());
    }

    @Test
    void processStartNewChat은_사용자의_인증정보가_없으면_NullPointerException을_반환한다() {
        when(currentUserProvider.authenticatedUser()).thenReturn(null);
        assertThrows(
                NullPointerException.class,
                () -> service.processStartNewChat(new NewChatCommand(1L, "TEST"))
        );
    }

    @Test
    void processStartNewChat은_사용자의_인증정보가_없으면_NullPointerException을_반환한다() {
        when(currentUserProvider.authenticatedUser()).thenReturn(null);
        assertThrows(
                NullPointerException.class,
                () -> service.processStartNewChat(new NewChatCommand(1L, "TEST"))
        );

    }

}
