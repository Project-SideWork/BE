package com.sidework.chat.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewChatCommand(
        @NotNull(message = "수신자 ID는 필수입니다.")
        @Positive(message = "수신자 ID는 양수입니다.")
        Long receiverId,
        @NotBlank(message = "채팅 내용은 필수입니다.")
        String content
) {
}
