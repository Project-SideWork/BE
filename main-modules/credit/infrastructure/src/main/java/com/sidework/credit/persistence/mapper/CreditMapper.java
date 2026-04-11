package com.sidework.credit.persistence.mapper;

import com.sidework.credit.domain.Credit;
import com.sidework.credit.persistence.entity.CreditEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit toDomain(CreditEntity entity);
    CreditEntity toEntity(Credit domain);
}
