package com.sidework.chat.application.adapter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewChatCommand(
        @NotNull(message = "수신자 ID는 필수입니다.")
        Long receiverId,
        @NotBlank(message = "채팅 내용은 필수입니다.")
        String content
) {
}
