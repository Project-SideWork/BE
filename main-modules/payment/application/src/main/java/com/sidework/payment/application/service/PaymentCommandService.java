package com.sidework.payment.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.payment.application.exception.SyncPaymentException;
import com.sidework.payment.application.port.in.CustomData;
import com.sidework.payment.application.port.in.Item;
import com.sidework.payment.application.port.in.PaymentCommandUseCase;
import com.sidework.payment.application.port.out.PaymentOutPort;
import com.sidework.payment.domain.Payment;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUseCase {
    private final PaymentClient portone;
    private final PaymentOutPort repo;
    private final ObjectMapper objectMapper;

    @Override
    public void create(Payment payment) {
        repo.save(payment);
    }

    @Override
    public void assignUser(Long userId, String paymentId) {
        Payment domain = repo.findById(paymentId);
        domain.assignUser(userId);
        repo.save(domain);
    }

    @Override
    public CompletableFuture<Payment> syncPayment(String paymentId) {
        return portone.getPayment(paymentId)
                .exceptionally(e -> { throw new SyncPaymentException(); })
                .thenApply(actualPayment -> {
                    if (!(actualPayment instanceof PaidPayment paidPayment)) {
                        throw new SyncPaymentException();
                    }

                    if (!verifyPayment(paidPayment)) throw new SyncPaymentException();

                    Payment domainPayment = Payment.create(
                            paidPayment.getId(),
                            paidPayment.getTransactionId(),
                            paidPayment.getStoreId(),
                            paidPayment.getOrderName(),
                            paidPayment.getAmount().getTotal(),
                            paidPayment.getCurrency().getValue(),
                            "PAID",
                            paidPayment.getCustomer().getName(),
                            paidPayment.getCustomer().getEmail(),
                            paidPayment.getCustomer().getPhoneNumber(),
                            "item1",
                            paidPayment.getPaidAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime(),
                            paidPayment.getRequestedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                    );

                    create(domainPayment);

                    return domainPayment;
                });
    }

    private boolean verifyPayment(PaidPayment payment) {
        // TODO: 실연동 시 변경
        //if (payment.getChannel().getType() != SelectedChannelType.Live.INSTANCE) return false;
        if (payment.getChannel().getType() != SelectedChannelType.Test.INSTANCE) return false;

        String customDataStr = payment.getCustomData();
        if (customDataStr == null) return false;

        try {
            CustomData customData = objectMapper.readValue(customDataStr, CustomData.class);

            // TODO: 결제 아이템 삽입 후 customData에서 꺼낸 값으로 쿼리 로직 추가
            Item item = new Item(
                    "ITEM_001", "신발", 1000, Currency.Krw.INSTANCE.getValue()
            );

            return payment.getOrderName().equals(item.name())
                    && payment.getAmount().getTotal() == item.price()
                    && payment.getCurrency().getValue().equals(item.currency());
        } catch (Exception e) {
            return false;
        }
    }
}
