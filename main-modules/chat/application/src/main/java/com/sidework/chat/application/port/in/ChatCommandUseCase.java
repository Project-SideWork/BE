package com.sidework.chat.application.port.in;

public interface ChatCommandUseCase {
    void processStartNewChat(Long senderId, NewChatCommand chatContent);
    void processResumeChat(Long chatRoomId, Long senderId, ExistChatCommand chatContent);
    void enterChatRoom(Long userId, Long chatRoomId);
    void leaveChatRoom(Long userId, Long chatRoomId);
}
