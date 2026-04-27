package com.sidework.chat.application.port.out;

import com.sidework.domain.ChatRoom;

import java.time.LocalDateTime;

public interface ChatRoomOutPort {
    Long save(ChatRoom chatRoom);
    boolean existsById(Long chatRoomId);
    int updateChatRoomLatest(
            String messageContent, LocalDateTime messageSendTime, Long lastMessageId, Long lastMessageSenderId
    );
}
