package com.sidework.user.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sidework.common.exception.InvalidCommandException;
import com.sidework.common.util.AesEncryptor;
import com.sidework.user.application.exception.GithubInfoNotFoundException;
import com.sidework.user.application.exception.UserNotFoundException;
import com.sidework.user.application.port.in.GithubInfoResponse;
import com.sidework.user.application.port.in.UserQueryUseCase;
import com.sidework.user.application.port.out.GithubInfoDto;
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
    private final AesEncryptor encryptor;

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
    public GithubInfoResponse queryGithubInformation(Long id) {
        if(id == null) throw new InvalidCommandException("사용자 ID는 필수값입니다.");
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        GithubInfoDto githubInfo = userRepository.findGithubInfoProjection(id);
        if(githubInfo.githubId() == null || githubInfo.githubAccessToken() == null) {
            throw new GithubInfoNotFoundException();
        }

        return new GithubInfoResponse(githubInfo.githubId(), githubInfo.githubLoginName(), encryptor.decrypt(githubInfo.githubAccessToken()));
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
