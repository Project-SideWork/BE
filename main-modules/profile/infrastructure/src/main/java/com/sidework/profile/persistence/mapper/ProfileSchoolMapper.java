package com.sidework.profile.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.persistence.entity.ProfileSchoolEntity;

@Mapper(componentModel = "spring")
public interface ProfileSchoolMapper {
	ProfileSchool toDomain(ProfileSchoolEntity entity);
	ProfileSchoolEntity toEntity(ProfileSchool domain);
}
