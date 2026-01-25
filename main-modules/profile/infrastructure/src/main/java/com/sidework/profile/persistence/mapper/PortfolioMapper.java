package com.sidework.profile.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.persistence.entity.PortfolioEntity;
import com.sidework.profile.persistence.entity.ProfileEntity;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
	PortfolioEntity toEntity(Portfolio portfolio);
	Portfolio toDomain(PortfolioEntity entity);
}
