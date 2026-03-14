package com.sidework.user.application.port.out;

import java.util.List;

import com.sidework.user.domain.User;

public interface UserOutPort {
    void save(User user);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByTel(String tel);
    boolean existsByEmailExcludingUserId(String email, Long excludeUserId);
    boolean existsByNicknameExcludingUserId(String nickname, Long excludeUserId);
    boolean existsByTelExcludingUserId(String tel, Long excludeUserId);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAllByUserIdIn(List<Long> ids);
}
