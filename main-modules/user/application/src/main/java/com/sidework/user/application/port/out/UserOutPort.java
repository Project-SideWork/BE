package com.sidework.user.application.port.out;

import java.util.List;

import com.sidework.user.domain.User;

public interface UserOutPort {
    Long save(User user);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByTel(String tel);
    boolean existsByEmailExcludingUserId(String email, Long excludeUserId);
    boolean existsByNicknameExcludingUserId(String nickname, Long excludeUserId);
    boolean existsByTelExcludingUserId(String tel, Long excludeUserId);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAllByUserIdIn(List<Long> ids);
    GithubInfoDto findGithubInfoProjection(Long userId);
    String findNameById(Long id);
}
