package com.sidework.chat.application.port.out;

import com.sidework.domain.ChatUser;

public interface ChatUserOutPort {
    void save(ChatUser chatUser);
}
