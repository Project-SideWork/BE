package com.sidework.chat.application.service;

import com.sidework.chat.application.port.in.NewChatCommand;
import com.sidework.chat.application.port.in.ChatCommandUseCase;
import com.sidework.chat.application.port.in.ExistChatCommand;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.common.event.sse.port.out.ChatMessageData;
import com.sidework.common.event.sse.port.out.SseSendOutPort;
import com.sidework.common.exception.ResourceUpdateFailedException;
import com.sidework.common.response.status.ErrorStatus;
import com.sidework.domain.ChatMessage;
import com.sidework.domain.ChatRoom;
import com.sidework.domain.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ChatCommandService implements ChatCommandUseCase {
    private final ChatMessageOutPort chatMessageRepository;
    private final ChatUserOutPort chatUserRepository;
    private final ChatRoomOutPort chatRoomRepository;
    private final SseSendOutPort sseSendAdapter;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void processStartNewChat(Long senderId, NewChatCommand chatCommand) {
        Long newChatRoomId = createNewChatRoom(chatCommand.content());
        LocalDateTime sendTime = Instant.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
        Long messageId = createNewChatMessage(newChatRoomId, senderId, chatCommand.content(), sendTime);

        createNewChatUser(newChatRoomId, senderId, messageId);
        createNewChatUser(newChatRoomId, chatCommand.receiverId(), null);

        if(chatRoomRepository.updateChatRoomLatest(chatCommand.content(), sendTime, messageId, senderId, newChatRoomId) == 0) {
            throw new ResourceUpdateFailedException(ErrorStatus.CHATROOM_UPDATE_ERROR);
        }

        sseSendAdapter.sendToUser(chatCommand.receiverId(), "MESSAGE_ARRIVED");
    }

    @Override
    public void processResumeChat(Long chatRoomId, Long senderId, ExistChatCommand chatCommand) {
        LocalDateTime sendTime = Instant.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
        Long messageId = createNewChatMessage(chatRoomId, senderId, chatCommand.content(), sendTime);
        Long pairUserId = chatUserRepository.findChatPairInRoom(senderId, chatRoomId);

        if(chatRoomRepository.updateChatRoomLatest(chatCommand.content(), sendTime, messageId, senderId, chatRoomId) == 0) {
            throw new ResourceUpdateFailedException(ErrorStatus.CHATROOM_UPDATE_ERROR);
        }

        if(chatUserRepository.updateLastReadChat(senderId, chatRoomId, messageId) == 0) {
            throw new ResourceUpdateFailedException(ErrorStatus.CHATUSER_UPDATE_ERROR);
        }

        // 수신자가 채팅방에 접속해있으면 알림 전송 X
        if(chatUserRepository.isChatRoomConnected(pairUserId, chatRoomId)) {
            sseSendAdapter.sendToChatRoom(chatRoomId, new ChatMessageData(messageId, chatCommand.content(), sendTime.toString(), senderId));
        }

        // 수신자가 채팅방에 없으면 알림 전송
        else{
            sseSendAdapter.sendToUser(pairUserId, "MESSAGE_ARRIVED");
        }
    }

    public void enterChatRoom(Long userId, Long chatRoomId) {
        chatUserRepository.updateIsConnected(userId, chatRoomId, true);
    }

    public void leaveChatRoom(Long userId, Long chatRoomId) {
        chatUserRepository.updateIsConnected(userId, chatRoomId, false);
    }

    public Long createNewChatRoom(String messageContent) {
        ChatRoom chatRoom = ChatRoom.create(messageContent);
        return chatRoomRepository.save(chatRoom); // 저장된 채팅방의 ID
    }

    public Long createNewChatMessage(Long chatRoomId, Long senderId, String content, LocalDateTime sendTime) {
        ChatMessage chatMessage = ChatMessage.create(chatRoomId, senderId, content, sendTime);
        return chatMessageRepository.save(chatMessage); // 저장된 채팅 메시지의 ID
    }

    public void createNewChatUser(Long chatRoomId, Long userId, Long lastReadChatId) {
        ChatUser chatUser = ChatUser.create(chatRoomId, userId, lastReadChatId);
        chatUserRepository.save(chatUser);
    }
}
