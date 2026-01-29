package com.sidework.skill.persistence.mapper;

import com.sidework.skill.domain.ProjectPreferredSkill;
import com.sidework.skill.persistence.entity.ProjectPreferredSkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectPreferredSkillMapper {
    ProjectPreferredSkill toDomain(ProjectPreferredSkillEntity entity);
    ProjectPreferredSkillEntity toEntity(ProjectPreferredSkill domain);
}
