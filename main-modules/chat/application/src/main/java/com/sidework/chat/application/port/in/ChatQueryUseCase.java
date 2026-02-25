package com.sidework.chat.application.port.in;


public interface ChatQueryUseCase {
    ChatMessageQueryResult queryMessagesByChatRoomId(Long chatRoomId, String cursor);
    void checkChatUserValidation(Long userId, Long chatRoomId);
}
