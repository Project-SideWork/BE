package com.sidework.credit.persistence.mapper;

import com.sidework.credit.domain.Credit;
import com.sidework.credit.persistence.entity.CreditEntity;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit toDomain(CreditEntity entity);
    CreditEntity toEntity(Credit domain);

    ZoneId PROJECT_ZONE = ZoneId.of("Asia/Seoul");

    default Instant map(LocalDate value) {
        if (value == null) return null;
        return value.plusDays(1)
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
