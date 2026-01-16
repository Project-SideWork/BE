package com.sidework.user.application.service;

import com.sidework.user.application.port.in.UserQueryUseCase;
import com.sidework.user.application.port.out.UserOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService implements UserQueryUseCase {
    private final UserOutPort userRepository;

    @Override
    public boolean checkEmailAvailable(String email) {
        return userRepository.existsByEmail(email);
    }
}
