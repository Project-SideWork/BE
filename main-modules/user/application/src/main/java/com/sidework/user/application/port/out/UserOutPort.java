package com.sidework.user.application.port.out;

import com.sidework.user.domain.User;

public interface UserOutPort {
    void save(User user);
    User findById(Long id);
}
