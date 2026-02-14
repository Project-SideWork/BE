package com.sidework.chat.application.port.in;

public interface ChatCommandUseCase {
    void processStartNewChat(ChatContent chatContent);
}
