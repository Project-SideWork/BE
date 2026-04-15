package com.sidework.payment.persistence.mapper;

import com.sidework.payment.domain.Subscription;
import com.sidework.payment.persistence.entity.SubscriptionEntity;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    Subscription toDomain(SubscriptionEntity entity);
    SubscriptionEntity toEntity(Subscription domain);

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
