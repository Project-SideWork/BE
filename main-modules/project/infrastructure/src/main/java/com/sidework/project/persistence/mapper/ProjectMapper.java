package com.sidework.project.persistence.mapper;

import com.sidework.project.domain.Project;
import com.sidework.project.persistence.entity.ProjectEntity;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectEntity toEntity(Project project);
    Project toDomain(ProjectEntity entity);

    default Instant map(LocalDate value) {
        if (value == null) return null;
        return value
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }

    default LocalDate map(Instant value) {
        if (value == null) return null;
        return value
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}