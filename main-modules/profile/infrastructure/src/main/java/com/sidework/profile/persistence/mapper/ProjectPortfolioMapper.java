package com.sidework.profile.persistence.mapper;

import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.profile.persistence.entity.ProjectPortfolioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectPortfolioMapper {
	ProjectPortfolio toDomain(ProjectPortfolioEntity entity);
	ProjectPortfolioEntity toEntity(ProjectPortfolio domain);
}

