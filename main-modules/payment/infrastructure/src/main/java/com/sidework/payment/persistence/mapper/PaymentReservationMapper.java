package com.sidework.payment.persistence.mapper;

import com.sidework.payment.domain.PaymentReservation;
import com.sidework.payment.persistence.entity.PaymentReservationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentReservationMapper {
    PaymentReservation toDomain(PaymentReservationEntity entity);
    PaymentReservationEntity toEntity(PaymentReservation domain);
}
