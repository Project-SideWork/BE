package com.sidework.chat.application.service;

import com.sidework.chat.application.port.in.ChatCommandUseCase;
import com.sidework.chat.application.adapter.ChatContent;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.common.auth.CurrentUserProvider;
import com.sidework.domain.ChatMessage;
import com.sidework.domain.ChatRoom;
import com.sidework.domain.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ChatCommandService implements ChatCommandUseCase {
    private final ChatMessageOutPort chatMessageRepository;
    private final ChatUserOutPort chatUserRepository;
    private final ChatRoomOutPort chatRoomRepository;
    private final CurrentUserProvider currentUserProvider;


    @Override
    public void processStartNewChat(ChatContent chatContent) {
        Long newChatRoom = createNewChatRoom();
        Long senderId = currentUserProvider.authenticatedUser().getId();
        Long messageId = createNewChatMessage(newChatRoom, senderId, chatContent.content());

        createNewChatUser(newChatRoom, senderId, messageId);
        createNewChatUser(newChatRoom, chatContent.receiverId(), null);
    }

    public Long createNewChatRoom() {
        ChatRoom chatRoom = ChatRoom.create();
        return chatRoomRepository.save(chatRoom); // 저장된 채팅방의 ID
    }

    public Long createNewChatMessage(Long chatRoomId, Long senderId, String content) {
        ChatMessage chatMessage = ChatMessage.create(chatRoomId, senderId, content);
        return chatMessageRepository.save(chatMessage); // 저장된 채팅 메시지의 ID
    }

    public void createNewChatUser(Long chatRoomId, Long userId, Long lastReadChatId) {
        ChatUser chatUser = ChatUser.create(chatRoomId, userId, lastReadChatId);
        chatUserRepository.save(chatUser);
    }
}
