package com.sidework.user.application.port.in;

import java.util.List;
import java.util.Map;

import com.sidework.user.domain.User;

public interface UserQueryUseCase {
    boolean checkEmailExists(String email);
    User findById(Long id);
    List<User> findByIdIn(List<Long> ids);
    Map<Long, String> findNamesByUserIds(List<Long> userIds);
}
