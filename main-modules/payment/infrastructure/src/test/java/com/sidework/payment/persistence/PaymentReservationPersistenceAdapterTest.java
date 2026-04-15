package com.sidework.payment.persistence;

import com.sidework.payment.domain.PaymentReservation;
import com.sidework.payment.persistence.adapter.PaymentReservationPersistenceAdapter;
import com.sidework.payment.persistence.entity.PaymentReservationEntity;
import com.sidework.payment.persistence.exception.PaymentNotFoundException;
import com.sidework.payment.persistence.mapper.PaymentReservationMapper;
import com.sidework.payment.persistence.repository.PaymentReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentReservationPersistenceAdapterTest {

    @Mock
    private PaymentReservationRepository repo;

    @Mock
    private PaymentReservationMapper mapper;

    @InjectMocks
    private PaymentReservationPersistenceAdapter adapter;

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        // given
        PaymentReservation domain = PaymentReservation.create(
                "payment-test-123",
                1L,
                2000,
                "READY"
        );

        PaymentReservationEntity entity = new PaymentReservationEntity();
        PaymentReservationEntity saved = PaymentReservationEntity.builder()
                .paymentId(domain.getPaymentId())
                .userId(domain.getUserId())
                .approvedCredit(domain.getApprovedCredit())
                .status(domain.getStatus())
                .build();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);

        // when
        adapter.save(domain);

        // then
        verify(mapper).toEntity(domain);
        verify(repo).save(entity);
    }

    @Test
    void findById는_Id로_예약결제를_조회해_도메인_객체로_변환한다() {
        // given
        PaymentReservation domain = new PaymentReservation(
                "payment-test-123",
                1L,
                2000,
                "READY"
        );

        PaymentReservationEntity entity = new PaymentReservationEntity(
                "payment-test-123",
                1L,
                2000,
                "READY"
        );

        when(repo.findById("payment-test-123")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // when
        PaymentReservation result = adapter.findById("payment-test-123");

        // then
        assertEquals(entity.getPaymentId(), result.getPaymentId());
        assertEquals(entity.getUserId(), result.getUserId());
        assertEquals(entity.getApprovedCredit(), result.getApprovedCredit());
        assertEquals(entity.getStatus(), result.getStatus());

        verify(repo).findById("payment-test-123");
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById는_Id를_가진_데이터가_없으면_PaymentNotFoundException을_던진다() {
        // given
        when(repo.findById("payment-test-123")).thenReturn(Optional.empty());

        // when & then
        assertThrows(
                PaymentNotFoundException.class,
                () -> adapter.findById("payment-test-123")
        );

        verify(repo).findById("payment-test-123");
    }
}