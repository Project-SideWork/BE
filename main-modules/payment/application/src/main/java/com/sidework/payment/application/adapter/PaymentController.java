package com.sidework.payment.application.adapter;

import com.sidework.payment.application.exception.SyncPaymentException;
import com.sidework.payment.application.port.in.CompletePaymentRequest;
import com.sidework.payment.application.port.in.CustomData;
import com.sidework.payment.application.port.in.Item;
import com.sidework.payment.application.port.in.PaymentCommandUseCase;
import com.sidework.payment.domain.Payment;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.PaymentClient;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentClient portone;
    private final WebhookVerifier portoneWebhook;
    private final PaymentCommandUseCase paymentCommandService;

    private static final com.fasterxml.jackson.databind.ObjectMapper objectMapper =
            new com.fasterxml.jackson.databind.ObjectMapper();


    @GetMapping("/item")
    public Item getItem() {
        return new Item(
                "ITEM_001", "신발", 1000, Currency.Krw.INSTANCE.getValue()
        );
    }

    @PostMapping("/complete")
    public CompletableFuture<Payment> completePayment(
            @RequestBody CompletePaymentRequest completeRequest) {
        return syncPayment(completeRequest.getPaymentId());
    }

    @PostMapping("/webhook")
    public CompletableFuture<Void> handleWebhook(
            @RequestBody String body,
            @RequestHeader("webhook-id") String webhookId,
            @RequestHeader("webhook-timestamp") String webhookTimestamp,
            @RequestHeader("webhook-signature") String webhookSignature) {

        return CompletableFuture.runAsync(() -> {
            Object webhook;
            try {
                webhook = portoneWebhook.verify(body, webhookId, webhookTimestamp, webhookSignature);
            } catch (Exception e) {
                throw new SyncPaymentException();
            }

            if (webhook instanceof WebhookTransaction) {
                syncPayment(((WebhookTransaction) webhook).getData().getPaymentId()).join();
            }
        });
    }


    public CompletableFuture<Payment> syncPayment(String paymentId) {
        log.info("paymentId: {}", paymentId);
        return CompletableFuture.supplyAsync(() -> {
            Object actualPayment;
            try {
                actualPayment = portone.getPayment(paymentId).join();
            } catch (Exception e) {
                throw new SyncPaymentException();
            }

            if (actualPayment instanceof PaidPayment paidPayment) {
                if (!verifyPayment(paidPayment)) throw new SyncPaymentException();
                log.info("결제 성공 {}", paidPayment);

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
                        paidPayment.getRequestedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime(),
                        paidPayment.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                );

                paymentCommandService.create(domainPayment);

                return domainPayment;

            } else {
                throw new SyncPaymentException();
            }
        });
    }

    public boolean verifyPayment(PaidPayment payment) {
        log.info("payment.getChannel().getType(): {}", payment.getChannel().getType());
        log.info("SelectedChannelType.Test.INSTANCE: {}", SelectedChannelType.Test.INSTANCE);
        //if (payment.getChannel().getType() != SelectedChannelType.Live.INSTANCE) return false;
        if (payment.getChannel().getType() != SelectedChannelType.Test.INSTANCE) return false;

        String customDataStr = payment.getCustomData();
        log.info("customDataStr: {}", customDataStr);
        if (customDataStr == null) return false;
        log.info("After customDataStr null check");

        try {
            CustomData customData = objectMapper.readValue(customDataStr, CustomData.class);
            log.info("customData: {}", customData.getItem());
            Item item = new Item(
                    "ITEM_001", "신발", 1000, Currency.Krw.INSTANCE.getValue()
            );
            log.info("item: {}", item.getName());

            return payment.getOrderName().equals(item.getName())
                    && payment.getAmount().getTotal() == item.getPrice()
                    && payment.getCurrency().getValue().equals(item.getCurrency());
        } catch (Exception e) {
            return false;
        }
    }
}
