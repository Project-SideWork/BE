package com.sidework.chat.application.port.in;


public interface ChatQueryUseCase {
    ChatMessageQueryResult queryMessagesByChatRoomId(Long chatRoomId, String cursor);
    void checkSubscribeValidation(Long userId, Long chatRoomId);
}
