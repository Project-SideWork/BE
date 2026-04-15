package com.sidework.payment.application.service;

import com.sidework.common.event.PaymentCompleteEvent;
import com.sidework.credit.application.port.out.CreditOutPort;
import com.sidework.payment.application.exception.PaymentProcessingException;
import com.sidework.payment.application.exception.SyncPaymentException;
import com.sidework.payment.application.port.in.*;
import com.sidework.payment.application.port.out.PaymentOutPort;
import com.sidework.payment.application.port.out.PaymentReservationOutPort;
import com.sidework.payment.domain.Payment;
import com.sidework.payment.domain.PaymentReservation;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentClient;
import io.portone.sdk.server.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCommandService implements PaymentCommandUseCase {
    private final ApplicationEventPublisher publisher;
    private final CreditOutPort creditRepo;
    private final PaymentClient portone;
    private final PaymentOutPort repo;
    private final PaymentReservationOutPort paymentReservationRepo;

    private static final int ITEM_PRICE = 10000;

    @Override
    public PreparePaymentResponse preparePayment(Long userId, PreparePaymentRequest request) {
        int usedCredit = Math.max(request.requestedCredit() != null ? request.requestedCredit() : 0, 0);

        int originalAmount = ITEM_PRICE;

        int availableCredit = creditRepo.findAmountByUser(userId);

        int approvedCredit = Math.min(usedCredit, availableCredit);
        approvedCredit = Math.min(approvedCredit, originalAmount);

        int finalAmount = originalAmount - approvedCredit;

        String paymentId = generatePaymentId();

        PaymentReservation reservation = PaymentReservation.create(
                paymentId,
                userId,
                approvedCredit
        );

        paymentReservationRepo.save(reservation);

        return new PreparePaymentResponse(
                paymentId,
                approvedCredit,
                finalAmount
        );
    }

    private String generatePaymentId() {
        return "payment-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID()
                .toString().substring(0, 8);
    }

    @Override
    public CompletableFuture<Payment> syncPayment(String paymentId) {
        PaymentReservation reservation = paymentReservationRepo.findById(paymentId);

        return portone.getPayment(paymentId)
                .exceptionally(e -> { throw new SyncPaymentException(); })
                .thenApply(actualPayment -> {
                    if (!(actualPayment instanceof PaidPayment paidPayment)) {
                        throw new SyncPaymentException();
                    }

                    if (!verifyPayment(paidPayment, reservation)) {
                        throw new SyncPaymentException();
                    }

                    Payment domainPayment = Payment.create(
                            paidPayment.getId(),
                            paidPayment.getTransactionId(),
                            paidPayment.getStoreId(),
                            paidPayment.getOrderName(),
                            ITEM_PRICE,
                            Math.toIntExact(paidPayment.getAmount().getTotal()),
                            paidPayment.getCurrency().getValue(),
                            PaymentStatus.Paid.INSTANCE.getValue(),
                            paidPayment.getCustomer().getName(),
                            paidPayment.getCustomer().getEmail(),
                            paidPayment.getCustomer().getPhoneNumber(),
                            "item1",
                            paidPayment.getPaidAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime(),
                            paidPayment.getRequestedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                    );

                    repo.save(domainPayment);
                    return domainPayment;
                });
    }

    @Override
    public void processAfterPaymentCompleted(Long userId, String paymentId) {
        try {
            Payment domain = repo.findById(paymentId);
            PaymentReservation reservation = paymentReservationRepo.findById(paymentId);

            if (domain.isAlreadyProcessed()) {
                log.info("Payment already processed. paymentId={}", paymentId);
                return;
            }

            domain.assignUser(userId);
            int usedCredit = repo.calculateUsedCredit(paymentId);

            domain.process();
            repo.save(domain);

            reservation.paid();
            paymentReservationRepo.save(reservation);

            publisher.publishEvent(new PaymentCompleteEvent(userId, usedCredit, paymentId));

        } catch (Exception e) {
            throw new PaymentProcessingException();
        }
    }

    private boolean verifyPayment(PaidPayment payment, PaymentReservation reservation) {
        // TODO: 실연동 시 변경
        //if (payment.getChannel().getType() != SelectedChannelType.Live.INSTANCE) return false;
        if (payment.getChannel().getType() != SelectedChannelType.Test.INSTANCE) return false;
        if (!"READY".equals(reservation.getStatus())) return false;

        int expectedAmount = ITEM_PRICE - reservation.getApprovedCredit();

        return "멤버십".equals(payment.getOrderName())
                && payment.getAmount().getTotal() == expectedAmount
                && Currency.Krw.INSTANCE.getValue().equals(payment.getCurrency().getValue());
    }
}
