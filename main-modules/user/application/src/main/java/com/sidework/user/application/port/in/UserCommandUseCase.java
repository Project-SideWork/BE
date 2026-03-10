package com.sidework.user.application.port.in;

public interface UserCommandUseCase {
    void signUp(SignUpCommand command);
    void updateMe(Long userId, String email, String name, String nickname, Integer age, String tel, Long residenceRegionId);
}
