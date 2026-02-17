package com.sidework.chat.application.port.in;

import com.sidework.chat.application.adapter.ExistChatCommand;
import com.sidework.chat.application.adapter.NewChatCommand;

public interface ChatCommandUseCase {
    void processStartNewChat(NewChatCommand chatContent);
    void processExistChat(Long chatRoomId, ExistChatCommand chatContent);
}
