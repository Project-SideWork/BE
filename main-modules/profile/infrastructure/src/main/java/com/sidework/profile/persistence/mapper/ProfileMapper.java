package com.sidework.profile.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.profile.domain.Profile;
import com.sidework.profile.persistence.entity.ProfileEntity;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
	ProfileEntity toEntity(Profile profile);
	Profile toDomain(ProfileEntity profile);
}
