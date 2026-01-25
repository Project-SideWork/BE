package com.sidework.profile.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.persistence.entity.ProfileSkillEntity;

@Mapper(componentModel = "spring")
public interface ProfileSkillMapper {
	ProfileSkill toDomain(ProfileSkillEntity entity);
	ProfileSkillEntity toEntity(ProfileSkill domain);
}
