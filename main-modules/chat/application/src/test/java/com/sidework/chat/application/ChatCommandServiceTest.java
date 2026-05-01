package com.sidework.chat.application;

import com.sidework.chat.application.port.in.ExistChatCommand;
import com.sidework.chat.application.port.in.NewChatCommand;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.chat.application.service.ChatCommandService;
import com.sidework.common.event.ChatRoomSseSendEvent;
import com.sidework.common.event.UserSseSendEvent;
import com.sidework.common.event.sse.port.out.ChatMessageData;
import com.sidework.common.event.sse.port.out.SseSendOutPort;
import com.sidework.common.exception.ResourceNotFoundException;
import com.sidework.common.exception.ResourceUpdateFailedException;
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
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatCommandServiceTest {

    @Mock
    private ChatMessageOutPort chatMessageRepository;

    @Mock
    private ChatUserOutPort chatUserRepository;

    @Mock
    private ChatRoomOutPort chatRoomRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ChatCommandService service;

    @Captor
    ArgumentCaptor<ChatUser> chatUserArgumentCaptor;

    @Captor
    ArgumentCaptor<UserSseSendEvent> userSseSendEventCaptor;

    @Captor
    ArgumentCaptor<ChatRoomSseSendEvent> chatRoomSseSendEventCaptor;

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

        Long chatMessageId = service.createNewChatMessage(
                1L,
                1L,
                "테스트",
                LocalDateTime.now()
        );

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
    void processStartNewChat은_모든_로직_성공_시_수신자에게_UserSseSendEvent를_발행한다() {
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(1L);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);
        when(chatRoomRepository.updateChatRoomLatest(
                anyString(),
                any(LocalDateTime.class),
                anyLong(),
                anyLong(),
                anyLong()
        )).thenReturn(1);

        service.processStartNewChat(1L, new NewChatCommand(2L, "테스트"));

        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(chatUserRepository, times(2)).save(any(ChatUser.class));

        verify(chatRoomRepository).updateChatRoomLatest(
                eq("테스트"),
                any(LocalDateTime.class),
                eq(1L),
                eq(1L),
                eq(1L)
        );

        verify(eventPublisher).publishEvent(userSseSendEventCaptor.capture());

        UserSseSendEvent event = userSseSendEventCaptor.getValue();

        assertEquals(2L, event.userId());
        assertEquals("MESSAGE_ARRIVED", event.data());
    }

    @Test
    void processStartNewChat은_채팅방_최신정보_업데이트_실패시_예외를_던지고_이벤트를_발행하지_않는다() {
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(1L);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);
        when(chatRoomRepository.updateChatRoomLatest(
                anyString(),
                any(LocalDateTime.class),
                anyLong(),
                anyLong(),
                anyLong()
        )).thenReturn(0);

        assertThrows(ResourceUpdateFailedException.class,
                () -> service.processStartNewChat(1L, new NewChatCommand(2L, "테스트")));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void processResumeChat은_상대_사용자를_찾지_못하면_ResourceNotFoundException을_던진다() {
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);
        when(chatUserRepository.findChatPairInRoom(1L, 1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> service.processResumeChat(1L, 1L, new ExistChatCommand("테스트")));

        verify(chatRoomRepository, never()).updateChatRoomLatest(
                anyString(),
                any(LocalDateTime.class),
                anyLong(),
                anyLong(),
                anyLong()
        );
        verify(chatUserRepository, never()).updateLastReadChat(anyLong(), anyLong(), anyLong());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void processResumeChat은_수신자가_채팅방에_접속중이면_ChatRoomSseSendEvent를_발행한다() {
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);
        when(chatUserRepository.findChatPairInRoom(1L, 1L)).thenReturn(2L);
        when(chatRoomRepository.updateChatRoomLatest(
                anyString(),
                any(LocalDateTime.class),
                anyLong(),
                anyLong(),
                anyLong()
        )).thenReturn(1);
        when(chatUserRepository.updateLastReadChat(1L, 1L, 1L)).thenReturn(1);
        when(chatUserRepository.isChatRoomConnected(2L, 1L)).thenReturn(true);

        service.processResumeChat(1L, 1L, new ExistChatCommand("테스트"));

        verify(chatMessageRepository).save(any(ChatMessage.class));

        verify(chatRoomRepository).updateChatRoomLatest(
                eq("테스트"),
                any(LocalDateTime.class),
                eq(1L),
                eq(1L),
                eq(1L)
        );

        verify(chatUserRepository).updateLastReadChat(1L, 1L, 1L);

        verify(eventPublisher).publishEvent(chatRoomSseSendEventCaptor.capture());

        ChatRoomSseSendEvent event = chatRoomSseSendEventCaptor.getValue();

        assertEquals(1L, event.chatRoomId());
        assertNotNull(event.data());
        assertEquals(1L, event.data().messageId());
        assertEquals("테스트", event.data().content());
        assertEquals(1L, event.data().senderUserId());
        assertNotNull(event.data().sendTime());

    }

    @Test
    void processResumeChat은_수신자가_채팅방에_없으면_UserSseSendEvent를_발행한다() {
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);
        when(chatUserRepository.findChatPairInRoom(1L, 1L)).thenReturn(2L);
        when(chatRoomRepository.updateChatRoomLatest(
                anyString(),
                any(LocalDateTime.class),
                anyLong(),
                anyLong(),
                anyLong()
        )).thenReturn(1);
        when(chatUserRepository.updateLastReadChat(1L, 1L, 1L)).thenReturn(1);
        when(chatUserRepository.isChatRoomConnected(2L, 1L)).thenReturn(false);

        service.processResumeChat(1L, 1L, new ExistChatCommand("테스트"));

        verify(eventPublisher).publishEvent(userSseSendEventCaptor.capture());

        UserSseSendEvent event = userSseSendEventCaptor.getValue();

        assertEquals(2L, event.userId());
        assertEquals("MESSAGE_ARRIVED", event.data());

    }

    @Test
    void processResumeChat은_채팅방_최신정보_업데이트_실패시_예외를_던지고_이벤트를_발행하지_않는다() {
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);
        when(chatUserRepository.findChatPairInRoom(1L, 1L)).thenReturn(2L);
        when(chatRoomRepository.updateChatRoomLatest(
                anyString(),
                any(LocalDateTime.class),
                anyLong(),
                anyLong(),
                anyLong()
        )).thenReturn(0);

        assertThrows(ResourceUpdateFailedException.class,
                () -> service.processResumeChat(1L, 1L, new ExistChatCommand("테스트")));

        verify(chatUserRepository, never()).updateLastReadChat(anyLong(), anyLong(), anyLong());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void processResumeChat은_보낸사람_lastReadChat_업데이트_실패시_예외를_던지고_이벤트를_발행하지_않는다() {
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(1L);
        when(chatUserRepository.findChatPairInRoom(1L, 1L)).thenReturn(2L);
        when(chatRoomRepository.updateChatRoomLatest(
                anyString(),
                any(LocalDateTime.class),
                anyLong(),
                anyLong(),
                anyLong()
        )).thenReturn(1);
        when(chatUserRepository.updateLastReadChat(1L, 1L, 1L)).thenReturn(0);

        assertThrows(ResourceUpdateFailedException.class,
                () -> service.processResumeChat(1L, 1L, new ExistChatCommand("테스트")));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void enterChatRoom은_isConnected를_true로_업데이트한다() {
        service.enterChatRoom(1L, 1L);

        verify(chatUserRepository).updateIsConnected(1L, 1L, true);
    }

    @Test
    void leaveChatRoom은_isConnected를_false로_업데이트한다() {
        service.leaveChatRoom(1L, 1L);

        verify(chatUserRepository).updateIsConnected(1L, 1L, false);
    }
}