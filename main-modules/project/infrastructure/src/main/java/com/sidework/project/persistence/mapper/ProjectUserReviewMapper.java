package com.sidework.project.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.project.domain.ProjectUserReview;
import com.sidework.project.persistence.entity.ProjectUserReviewEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ProjectUserReviewMapper {
    ZoneId PROJECT_ZONE = ZoneId.of("Asia/Seoul");

	ProjectUserReview toDomain(ProjectUserReviewEntity entity);
	ProjectUserReviewEntity toEntity(ProjectUserReview domain);

    default Instant map(LocalDate value) {
        if (value == null) return null;
        return value
                .atStartOfDay(PROJECT_ZONE)
                .toInstant();
    }

    default LocalDate map(Instant value) {
        if (value == null) return null;
        return value
                .atZone(PROJECT_ZONE)
                .toLocalDate();
    }
}
