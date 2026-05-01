package com.sidework.common.event;

public record UserSseSendEvent(
        Long userId,
        String data
) implements SseSendEvent {

}