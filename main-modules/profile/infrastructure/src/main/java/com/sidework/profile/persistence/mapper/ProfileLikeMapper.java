package com.sidework.profile.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.profile.domain.ProfileLike;
import com.sidework.profile.persistence.entity.ProfileLikeEntity;

@Mapper(componentModel = "spring")
public interface ProfileLikeMapper {
	ProfileLike toDomain(ProfileLikeEntity entity);
	ProfileLikeEntity toEntity(ProfileLike entity);
}

