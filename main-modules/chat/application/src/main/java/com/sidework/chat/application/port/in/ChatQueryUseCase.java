package com.sidework.chat.application.port.in;


public interface ChatQueryUseCase {
    ChatMessageQueryResult queryByChatRoomId(Long chatRoomId, String cursor);
}
