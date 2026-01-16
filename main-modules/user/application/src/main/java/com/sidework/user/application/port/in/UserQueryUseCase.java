package com.sidework.user.application.port.in;

public interface UserQueryUseCase {
    boolean checkEmailAvailable(String email);
}
