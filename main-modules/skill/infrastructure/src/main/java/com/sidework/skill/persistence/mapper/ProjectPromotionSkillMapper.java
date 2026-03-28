package com.sidework.skill.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.skill.domain.ProjectPromotionSkill;
import com.sidework.skill.persistence.entity.ProjectPromotionSkillEntity;

@Mapper(componentModel = "spring")
public interface ProjectPromotionSkillMapper {

	ProjectPromotionSkill toDomain(ProjectPromotionSkillEntity entity);
	ProjectPromotionSkillEntity toEntity(ProjectPromotionSkill domain);
}
