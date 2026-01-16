package com.sidework.user.application.port.in;

import com.sidework.user.application.adapter.SignUpCommand;

public interface UserCommandUseCase {
    void signUp(SignUpCommand command);
}
