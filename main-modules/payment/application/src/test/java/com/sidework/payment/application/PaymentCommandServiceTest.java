package com.sidework.payment.application;

import com.sidework.payment.application.port.out.PaymentOutPort;
import com.sidework.payment.application.service.PaymentCommandService;
import com.sidework.payment.domain.Payment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock
    private PaymentOutPort repo;

    @InjectMocks
    private PaymentCommandService service;

    @Captor
    private ArgumentCaptor<Payment> captor;

    @Test
    void create는_도메인_객체를_전달받아_저장한다() {
        doNothing().when(repo).save(any(Payment.class));

        Payment domain = makePayment();

        service.create(domain);

        verify(repo).save(captor.capture());
        Payment saved = captor.getValue();

        assertEquals(domain.getPaymentId(),     saved.getPaymentId());
        assertEquals(domain.getTransactionId(), saved.getTransactionId());
        assertEquals(domain.getStoreId(),       saved.getStoreId());
        assertEquals(domain.getOrderName(),     saved.getOrderName());
        assertEquals(domain.getAmount(),        saved.getAmount());
        assertEquals(domain.getCurrency(),      saved.getCurrency());
        assertEquals(domain.getStatus(),        saved.getStatus());
        assertEquals(domain.getCustomerName(),  saved.getCustomerName());
        assertEquals(domain.getCustomerEmail(), saved.getCustomerEmail());
        assertEquals(domain.getCustomerPhone(), saved.getCustomerPhone());
        assertEquals(domain.getItemId(),        saved.getItemId());
        assertEquals(domain.getPaidAt(),        saved.getPaidAt());
        assertEquals(domain.getRequestedAt(),   saved.getRequestedAt());
    }



    private Payment makePayment() {
        return Payment.create(
                "payment-test-123", "tx-test-123", "store-test-123",
                "신발", 1000L, "KRW", "PAID",
                "홍길동", "test@example.com", "01012345678", "item1",
                LocalDateTime.of(2026, 4, 1, 15, 0),
                LocalDateTime.of(2026, 4, 1, 14, 50),
                LocalDateTime.of(2026, 4, 1, 15, 1)
        );
    }
}
