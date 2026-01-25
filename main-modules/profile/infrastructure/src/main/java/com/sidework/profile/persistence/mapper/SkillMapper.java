package com.sidework.profile.persistence.mapper;

import com.sidework.profile.domain.Skill;
import com.sidework.profile.persistence.entity.SkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
	Skill toDomain(SkillEntity entity);
	SkillEntity toEntity(Skill domain);
}

