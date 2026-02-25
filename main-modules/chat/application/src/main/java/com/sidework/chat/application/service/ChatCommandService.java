package com.sidework.chat.application.service;

import com.sidework.chat.application.port.in.NewChatCommand;
import com.sidework.chat.application.port.in.ChatCommandUseCase;
import com.sidework.chat.application.port.in.ExistChatCommand;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.common.event.sse.port.out.SseSendOutPort;
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
    private final SseSendOutPort sseSendAdapter;


    @Override
    public void processStartNewChat(Long senderId, NewChatCommand chatCommand) {
        Long newChatRoom = createNewChatRoom(chatCommand.content());
        Long messageId = createNewChatMessage(newChatRoom, senderId, chatCommand.content());

        createNewChatUser(newChatRoom, senderId, messageId);
        createNewChatUser(newChatRoom, chatCommand.receiverId(), null);

        sseSendAdapter.sendToUser(chatCommand.receiverId(), chatCommand.content());
    }

    @Override
    public void processResumeChat(Long chatRoomId, Long senderId,ExistChatCommand chatCommand) {
        Long messageId = createNewChatMessage(chatRoomId, senderId, chatCommand.content());
        chatUserRepository.updateLastReadChat(senderId, chatRoomId, messageId);

        // TODO: 사용자가 채팅방에 없는 경우에는 알림함으로 보내게 할 수 있도록 판단하는 로직 추가 필요.
        sseSendAdapter.sendToChatRoom(chatRoomId, chatCommand.content());
    }

    public Long createNewChatRoom(String messageContent) {
        ChatRoom chatRoom = ChatRoom.create(messageContent);
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
