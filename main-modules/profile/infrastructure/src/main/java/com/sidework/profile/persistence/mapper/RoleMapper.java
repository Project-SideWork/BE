package com.sidework.profile.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.profile.domain.Role;
import com.sidework.profile.persistence.entity.RoleEntity;

@Mapper(componentModel = "spring")
public interface RoleMapper
{
	RoleEntity toEntity(Role role);
	Role toDomain(RoleEntity role);

}
