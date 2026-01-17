package com.sidework.user.persistence.mapper;

import com.sidework.user.domain.User;
import com.sidework.user.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User user);
}
