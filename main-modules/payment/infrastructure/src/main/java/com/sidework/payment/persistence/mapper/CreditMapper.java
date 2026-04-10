package com.sidework.payment.persistence.mapper;

import com.sidework.payment.domain.Credit;
import com.sidework.payment.persistence.entity.CreditEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit toDomain(CreditEntity entity);
    CreditEntity toEntity(Credit domain);
}
