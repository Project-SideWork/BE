package com.sidework.user.application.port.out;

import java.util.List;

import com.sidework.user.domain.User;

public interface UserOutPort {
    void save(User user);
    boolean existsByEmail(String email);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAllByUserIdIn(List<Long> ids);
}
