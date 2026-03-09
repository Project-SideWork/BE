package com.sidework.region.persistence.mapper;

import com.sidework.region.domain.Region;
import com.sidework.region.persistence.entity.RegionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegionMapper {
    Region toDomain(RegionEntity entity);
    RegionEntity toEntity(Region entity);
}
