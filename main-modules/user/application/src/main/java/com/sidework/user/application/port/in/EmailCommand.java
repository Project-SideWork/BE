package com.sidework.user.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailCommand(
        @Email @NotNull String email
) {
}
