package com.sidework.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.event.PaymentCompleteEvent;
import com.sidework.payment.application.port.in.CustomData;
import com.sidework.payment.application.port.out.PaymentOutPort;
import com.sidework.payment.application.service.PaymentCommandService;
import com.sidework.payment.domain.Payment;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private PaymentOutPort repo;

    @InjectMocks
    private PaymentCommandService service;

    @Captor
    private ArgumentCaptor<Payment> captor;


    @Test
    void processAfterPaymentCompleted는_유저를_할당하고_이벤트를_발행한다() {
        // given
        Long userId = 1L;
        String paymentId = "payment-test-123";
        Payment domain = makePayment();

        when(repo.findById(paymentId)).thenReturn(domain);
        doNothing().when(repo).save(any(Payment.class));
        when(repo.calculateUsedCredit(paymentId)).thenReturn(0);

        // when
        service.processAfterPaymentCompleted(userId, paymentId);

        // then
        verify(repo).findById(paymentId);
        verify(repo).save(captor.capture());
        assertNotNull(captor.getValue().getUserId());

        verify(publisher).publishEvent(any(PaymentCompleteEvent.class));
    }

    private Payment makePayment() {
        return Payment.create(
                "payment-test-123", "tx-test-123", "store-test-123",
                "멤버십", 10000, 10000, "KRW", "PAID",
                "홍길동", "test@example.com", "01012345678", "item1",
                LocalDateTime.of(2026, 4, 1, 15, 0),
                LocalDateTime.of(2026, 4, 1, 14, 50)
        );
    }
}