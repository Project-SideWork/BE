package com.sidework.payment.persistence;

import com.sidework.payment.domain.Payment;
import com.sidework.payment.persistence.adapter.PaymentPersistenceAdapter;
import com.sidework.payment.persistence.entity.PaymentEntity;
import com.sidework.payment.persistence.exception.PaymentNotFoundException;
import com.sidework.payment.persistence.mapper.PaymentMapper;
import com.sidework.payment.persistence.repository.PaymentJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentPersistenceAdapterTest {
    @Mock
    private PaymentJpaRepository repo;

    @Mock
    private PaymentMapper mapper;

    @InjectMocks
    private PaymentPersistenceAdapter adapter;


    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        Payment domain = Payment.create(
                "payment-test-123",
                "tx-test-123",
                "store-test-123",
                "테스트 상품",
                1000,
                1000,
                "KRW",
                "PAID",
                "홍길동",
                "test@example.com",
                "01012345678",
                "item1",
                LocalDateTime.of(2026, 4, 1, 15, 0),
                LocalDateTime.of(2026, 4, 1, 14, 50)
        );

        PaymentEntity entity = new PaymentEntity();
        PaymentEntity saved = PaymentEntity.builder()
                .paymentId(domain.getPaymentId())
                .transactionId(domain.getTransactionId())
                .storeId(domain.getStoreId())
                .orderName(domain.getOrderName())
                .amount(domain.getAmount())
                .currency(domain.getCurrency())
                .status(domain.getStatus())
                .customerName(domain.getCustomerName())
                .customerEmail(domain.getCustomerEmail())
                .customerPhone(domain.getCustomerPhone())
                .itemId(domain.getItemId())
                .paidAt(domain.getPaidAt().atZone(ZoneId.of("Asia/Seoul")).toInstant())
                .requestedAt(domain.getRequestedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant())
                .build();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);

        adapter.save(domain);

        verify(mapper).toEntity(domain);
        verify(repo).save(entity);
    }

    @Test
    void findById는_Id로_결제를_조회해_도메인_객체로_변환한다() {
        Payment domain = new Payment(
                "payment-test-123",
                1L,
                "tx-test-123",
                "store-test-123",
                "테스트 상품",
                1000,
                1000,
                "KRW",
                "PAID",
                "홍길동",
                "test@example.com",
                "01012345678",
                "item1",
                true,
                LocalDateTime.of(2026, 4, 1, 15, 0),
                LocalDateTime.of(2026, 4, 1, 14, 50)
        );

        PaymentEntity entity = new PaymentEntity(
                "payment-test-123",
                1L,
                "tx-test-123",
                "store-test-123",
                "테스트 상품",
                1000,
                1000,
                "KRW",
                "PAID",
                "홍길동",
                "test@example.com",
                "01012345678",
                "item1",
                true,
                Instant.now(),
                Instant.now()
        );



        when(repo.findById("payment-test-123")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Payment res = adapter.findById("payment-test-123");

        assertEquals(res.getPaymentId(), entity.getPaymentId());

        verify(repo).findById("payment-test-123");
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById는_Id를_가진_데이터가_없으면_PaymentNotFoundException을_던진다() {
        when(repo.findById("payment-test-123")).thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> adapter.findById("payment-test-123")
        );

        verify(repo).findById("payment-test-123");
    }

    @Test
    void calculateUsedCredit는_paymentId로_사용된_크레딧을_반환한다() {
        String paymentId = "payment-test-123";
        int expectedCredit = 990;

        when(repo.findUsedCreditById(paymentId)).thenReturn(expectedCredit);

        int result = adapter.calculateUsedCredit(paymentId);

        assertEquals(expectedCredit, result);
        verify(repo).findUsedCreditById(paymentId);
    }
}
