package com.sidework.skill.persistence.mapper;

import com.sidework.skill.domain.ProjectRequiredSkill;
import com.sidework.skill.persistence.entity.ProjectRequiredSkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectRequiredSkillMapper {
    ProjectRequiredSkill toDomain(ProjectRequiredSkillEntity entity);
    ProjectRequiredSkillEntity toEntity(ProjectRequiredSkill domain);
}
