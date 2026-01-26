package com.sidework.user.application.port.in;

import com.sidework.user.domain.User;

public interface UserQueryUseCase {
    boolean checkEmailExists(String email);
    User findById(Long id);
}
