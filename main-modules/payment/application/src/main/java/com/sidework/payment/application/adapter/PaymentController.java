package com.sidework.payment.application.adapter;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.payment.application.exception.SyncPaymentException;
import com.sidework.payment.application.port.in.CompletePaymentRequest;
import com.sidework.payment.application.port.in.Item;
import com.sidework.payment.application.port.in.PaymentCommandUseCase;
import com.sidework.payment.domain.Payment;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final WebhookVerifier portoneWebhook;
    private final PaymentCommandUseCase paymentCommandService;

    // TODO: DB에 저장된 구매 아이템 조회 API로 변경
    @GetMapping("/item")
    public Item getItem() {
        return new Item(
                "ITEM_001", "신발", 1000, Currency.Krw.INSTANCE.getValue()
        );
    }

    @PostMapping("/complete")
    public CompletableFuture<Payment> completePayment(
            @AuthenticationPrincipal AuthenticatedUserDetails details,
            @RequestBody CompletePaymentRequest completeRequest) {
        return paymentCommandService.syncPayment(completeRequest.paymentId())
                .thenApply(payment -> {
            paymentCommandService.assignUser(details.getId(), payment.getPaymentId());
            return payment;
                });
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
                paymentCommandService.syncPayment(((WebhookTransaction) webhook).getData().getPaymentId()).join();
            }
        });
    }
}
