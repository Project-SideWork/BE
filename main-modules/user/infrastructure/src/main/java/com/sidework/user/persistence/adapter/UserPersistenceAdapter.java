package com.sidework.user.persistence.adapter;

import java.util.List;

import com.sidework.user.application.port.out.UserOutPort;
import com.sidework.user.domain.User;
import com.sidework.user.persistence.entity.UserEntity;
import com.sidework.user.persistence.exception.UserNotFoundException;
import com.sidework.user.persistence.mapper.UserMapper;
import com.sidework.user.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserOutPort {
    private final UserJpaRepository repo;
    private final UserMapper mapper;

    @Override
    public void save(User user) {
        repo.save(mapper.toEntity(user));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    @Override
    public User findById(Long id) {
        UserEntity user = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapper.toDomain(user);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity user = repo.findByEmail(email);
        if(user == null) throw new UserNotFoundException(email);
        return mapper.toDomain(user);
    }

    @Override
    public List<User> findAllByUserIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<UserEntity> users = repo.findAllById(ids);
        return users.stream().map(mapper::toDomain).toList();
    }
}
