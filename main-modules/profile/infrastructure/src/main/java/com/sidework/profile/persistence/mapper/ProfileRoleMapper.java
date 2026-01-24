package com.sidework.profile.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.persistence.entity.ProfileRoleEntity;
import com.sidework.profile.persistence.entity.RoleEntity;

@Mapper(componentModel = "spring")
public interface ProfileRoleMapper
{
	ProfileRole toDomain(ProfileRoleEntity entity);
	ProfileRoleEntity toEntity (ProfileRole domain);
}
