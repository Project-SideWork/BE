package com.sidework.project.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.persistence.entity.ProjectPromotionEntity;

@Mapper(componentModel = "spring")
public interface ProjectPromotionMapper {
	ProjectPromotion toDomain(ProjectPromotionEntity entity);
	ProjectPromotionEntity toEntity(ProjectPromotion domain);
}
