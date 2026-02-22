package com.sidework.chat.application.adapter;

import jakarta.validation.constraints.NotBlank;

public record ExistChatCommand(
        @NotBlank(message = "채팅 내용은 필수입니다.")
        String content
) {
}
