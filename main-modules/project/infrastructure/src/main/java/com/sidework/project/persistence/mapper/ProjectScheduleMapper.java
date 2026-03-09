package com.sidework.project.persistence.mapper;

import com.sidework.project.domain.ProjectSchedule;
import com.sidework.project.persistence.entity.ProjectScheduleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectScheduleMapper {
    ProjectSchedule toDomain(ProjectScheduleEntity entity);
    ProjectScheduleEntity toEntity(ProjectSchedule domain);
}
