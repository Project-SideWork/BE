package com.sidework.chat.application.port.out;

import com.sidework.domain.ChatRoom;

public interface ChatRoomOutPort {
    Long save(ChatRoom chatRoom);
}
