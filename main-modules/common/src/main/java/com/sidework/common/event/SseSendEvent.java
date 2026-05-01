package com.sidework.common.event;


public sealed interface SseSendEvent permits UserSseSendEvent, ChatRoomSseSendEvent {
}
