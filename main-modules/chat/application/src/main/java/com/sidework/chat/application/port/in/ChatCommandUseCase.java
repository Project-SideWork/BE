package com.sidework.chat.application.port.in;

import com.sidework.chat.application.adapter.ExistChatCommand;
import com.sidework.chat.application.adapter.NewChatCommand;

public interface ChatCommandUseCase {
    void processStartNewChat(Long senderId, NewChatCommand chatContent);
    void processResumeChat(Long chatRoomId, Long senderId, ExistChatCommand chatContent);
}
