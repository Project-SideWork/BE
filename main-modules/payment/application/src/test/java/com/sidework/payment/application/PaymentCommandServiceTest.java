package com.sidework.payment.application;

import com.sidework.common.event.PaymentCompleteEvent;
import com.sidework.credit.application.port.out.CreditOutPort;
import com.sidework.payment.application.port.in.PreparePaymentRequest;
import com.sidework.payment.application.port.in.PreparePaymentResponse;
import com.sidework.payment.application.port.out.PaymentOutPort;
import com.sidework.payment.application.port.out.PaymentReservationOutPort;
import com.sidework.payment.application.service.PaymentCommandService;
import com.sidework.payment.domain.Payment;
import com.sidework.payment.domain.PaymentReservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private CreditOutPort creditRepo;

    @Mock
    private PaymentOutPort repo;

    @Mock
    private PaymentReservationOutPort paymentReservationRepo;

    @InjectMocks
    private PaymentCommandService service;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    @Captor
    private ArgumentCaptor<PaymentCompleteEvent> eventCaptor;

    @Test
    void processAfterPaymentCompleted는_유저를_할당하고_예약을_처리하고_이벤트를_발행한다() {
        // given
        Long userId = 1L;
        String paymentId = "payment-test-123";
        Payment payment = makePayment();
        PaymentReservation reservation = makeReservation(paymentId, userId, 2000, "READY");

        when(repo.findById(paymentId)).thenReturn(payment);
        when(paymentReservationRepo.findById(paymentId)).thenReturn(reservation);
        when(repo.calculateUsedCredit(paymentId)).thenReturn(2000);

        // when
        service.processAfterPaymentCompleted(userId, paymentId);

        // then
        verify(repo).findById(paymentId);
        verify(paymentReservationRepo).findById(paymentId);

        verify(repo).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        assertNotNull(savedPayment.getUserId());

        verify(paymentReservationRepo).save(reservation);

        verify(publisher).publishEvent(eventCaptor.capture());
    }

    @Test
    void preparePayment는_요청한_크레딧이_보유량_이하이면_그대로_승인한다() {
        // given
        Long userId = 1L;
        PreparePaymentRequest request = new PreparePaymentRequest(2000);

        when(creditRepo.findAmountByUser(userId)).thenReturn(5000);

        // when
        PreparePaymentResponse response = service.preparePayment(userId, request);

        // then
        assertNotNull(response.paymentId());
        assertEquals(2000, response.approvedCredit());
        assertEquals(8000, response.finalAmount());

        verify(creditRepo).findAmountByUser(userId);
        verify(paymentReservationRepo).save(any(PaymentReservation.class));
    }

    @Test
    void preparePayment는_보유_크레딧보다_많이_요청하면_보유량까지만_승인한다() {
        // given
        Long userId = 1L;
        PreparePaymentRequest request = new PreparePaymentRequest(5000);

        when(creditRepo.findAmountByUser(userId)).thenReturn(1000);

        // when
        PreparePaymentResponse response = service.preparePayment(userId, request);

        // then
        assertNotNull(response.paymentId());
        assertEquals(1000, response.approvedCredit());
        assertEquals(9000, response.finalAmount());

        verify(paymentReservationRepo).save(any(PaymentReservation.class));
    }

    @Test
    void preparePayment는_음수_크레딧_요청이면_0으로_보정한다() {
        // given
        Long userId = 1L;
        PreparePaymentRequest request = new PreparePaymentRequest(-1000);

        when(creditRepo.findAmountByUser(userId)).thenReturn(5000);

        // when
        PreparePaymentResponse response = service.preparePayment(userId, request);

        // then
        assertNotNull(response.paymentId());
        assertEquals(0, response.approvedCredit());
        assertEquals(10000, response.finalAmount());

        verify(paymentReservationRepo).save(any(PaymentReservation.class));
    }

    private Payment makePayment() {
        return Payment.create(
                "payment-test-123", "tx-test-123", "store-test-123",
                "멤버십", 10000, 8000, "KRW", "PAID",
                "홍길동", "test@example.com", "01012345678", "item1",
                LocalDateTime.of(2026, 4, 1, 15, 0),
                LocalDateTime.of(2026, 4, 1, 14, 50)
        );
    }

    private PaymentReservation makeReservation(String paymentId, Long userId, int approvedCredit, String status) {
        return PaymentReservation.create(paymentId, userId, approvedCredit, status);
    }
}