package com.sidework.user.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerificationCodeCommand(
        @NotNull @Email String email,
        @NotBlank String code) {
}
