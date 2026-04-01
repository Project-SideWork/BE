package com.sidework.payment.persistence.mapper;

import com.sidework.common.util.DateTimeMapper;
import com.sidework.payment.domain.Payment;
import com.sidework.payment.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface PaymentMapper {
    Payment toDomain(PaymentEntity entity);
    PaymentEntity toEntity(Payment domain);
}
