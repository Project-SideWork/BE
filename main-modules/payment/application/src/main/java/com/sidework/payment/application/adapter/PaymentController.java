package com.sidework.payment.application.adapter;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.payment.application.exception.SyncPaymentException;
import com.sidework.payment.application.port.in.CompletePaymentRequest;
import com.sidework.payment.application.port.in.Item;
import com.sidework.payment.application.port.in.PaymentCommandUseCase;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final WebhookVerifier portoneWebhook;
    private final PaymentCommandUseCase paymentCommandService;

    @GetMapping("/item")
    public Item getItem() {
        return new Item(
                "ITEM_001", "멤버십", 10000, Currency.Krw.INSTANCE.getValue()
        );
    }

    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<Void>> completePayment(
            @AuthenticationPrincipal AuthenticatedUserDetails details,
            @RequestBody CompletePaymentRequest completeRequest) {
        paymentCommandService.syncPayment(completeRequest.paymentId()).join();
        paymentCommandService.processAfterPaymentCompleted(details.getId(), completeRequest.paymentId());
        return ResponseEntity.ok(ApiResponse.onSuccessVoid());
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
