package com.sidework.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.payment.application.adapter.PaymentController;
import com.sidework.payment.application.port.in.PaymentCommandUseCase;
import com.sidework.payment.application.port.in.PreparePaymentRequest;
import com.sidework.payment.application.port.in.PreparePaymentResponse;
import io.portone.sdk.server.webhook.WebhookVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PaymentController.class)
@ContextConfiguration(classes = PaymentTestApplication.class)
@Import(ExceptionAdvice.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentCommandUseCase paymentCommandService;

    @MockitoBean
    private WebhookVerifier portoneWebhook;


    private final AuthenticatedUserDetails authenticatedUserDetails =
            new AuthenticatedUserDetails(1L, "test@test.com", "테스터", "password");

    @Test
    void 상품_조회_요청시_성공하면_200과_상품정보를_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/payments/item")
                        .with(user(authenticatedUserDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ITEM_001"))
                .andExpect(jsonPath("$.name").value("멤버십"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.currency").value("KRW"));
    }

    @Test
    void 결제_준비_요청시_성공하면_200과_prepare응답을_반환한다() throws Exception {
        PreparePaymentRequest request = new PreparePaymentRequest(2000);
        PreparePaymentResponse response = new PreparePaymentResponse(
                "payment-test-123",
                2000,
                8000
        );

        when(paymentCommandService.preparePayment(authenticatedUserDetails.getId(), request))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/payments/prepare")
                        .with(user(authenticatedUserDetails))
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.paymentId").value("payment-test-123"))
                .andExpect(jsonPath("$.result.approvedCredit").value(2000))
                .andExpect(jsonPath("$.result.finalAmount").value(8000));

        verify(paymentCommandService).preparePayment(authenticatedUserDetails.getId(), request);
    }
}
