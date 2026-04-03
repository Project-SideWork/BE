package com.sidework.payment.persistence;

import com.sidework.payment.domain.Payment;
import com.sidework.payment.persistence.adapter.PaymentPersistenceAdapter;
import com.sidework.payment.persistence.entity.PaymentEntity;
import com.sidework.payment.persistence.mapper.PaymentMapper;
import com.sidework.payment.persistence.repository.PaymentJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
                1000L,
                "KRW",
                "PAID",
                "홍길동",
                "test@example.com",
                "01012345678",
                "item1",
                LocalDateTime.of(2026, 4, 1, 15, 0),
                LocalDateTime.of(2026, 4, 1, 14, 50),
                LocalDateTime.of(2026, 4, 1, 15, 1)
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
}
