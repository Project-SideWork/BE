package com.sidework.skill.persistence.mapper;

import com.sidework.skill.domain.Skill;
import com.sidework.skill.persistence.entity.SkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toDomain(SkillEntity entity);
    SkillEntity toEntity(Skill domain);
}
