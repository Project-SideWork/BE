package com.sidework.chat.application.port.in;

import com.sidework.chat.application.adapter.ChatContent;

public interface ChatCommandUseCase {
    void processStartNewChat(ChatContent chatContent);
}
