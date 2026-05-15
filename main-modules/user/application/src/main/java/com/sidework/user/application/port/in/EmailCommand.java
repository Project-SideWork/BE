package com.sidework.user.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCommand(
        @Email @NotBlank String email
) {
}
