package com.sidework.user.application.port.in;

import jakarta.validation.constraints.*;

public record SignUpCommand(
        @NotNull @Email String email,
        @NotNull @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.") String password,
        @NotNull String name,
        @NotNull String nickname,
        @NotNull Integer age,
        @NotNull String tel,
        @NotNull @Positive Long residenceRegionId
) {
}
