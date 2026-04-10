package com.sidework.payment.persistence.mapper;

import com.sidework.payment.domain.Subscription;
import com.sidework.payment.persistence.entity.SubscriptionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    Subscription toDomain(SubscriptionEntity entity);
    SubscriptionEntity toEntity(Subscription domain);
}
