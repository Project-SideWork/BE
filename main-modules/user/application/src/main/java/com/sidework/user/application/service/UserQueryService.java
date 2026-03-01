package com.sidework.user.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sidework.user.application.port.in.UserQueryUseCase;
import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements UserQueryUseCase {
    private final UserOutPort userRepository;

    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return userRepository.findAllByUserIdIn(ids);
    }

    @Override
    public void validateExists(Long id) {
        userRepository.findById(id);
    }

    @Override
    public Map<Long, String> findNamesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(User::getId, User::getName, (a, b) -> a));
    }
}
