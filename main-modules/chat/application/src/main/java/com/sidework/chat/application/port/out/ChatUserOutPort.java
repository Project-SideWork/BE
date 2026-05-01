package com.sidework.chat.application.port.out;

import com.sidework.domain.ChatUser;

import java.time.Instant;

public interface ChatUserOutPort {
    void save(ChatUser chatUser);
    int updateLastReadChat(Long userId, Long chatRoomId, Long chatMessageId);
    void updateIsConnected(Long userId, Long chatRoomId, boolean isConnected);
    boolean existsByUserAndRoom(Long userId, Long chatRoomId);
    boolean isChatRoomConnected(Long userId, Long chatRoomId);
    Long findChatPairInRoom(Long senderId, Long chatRoomId);
    ChatUserSummaryPage findByUserIdAndIdGreaterThan(
            Long userId, Instant cursorCreatedAt, Long cursorId, int size
    );
}
