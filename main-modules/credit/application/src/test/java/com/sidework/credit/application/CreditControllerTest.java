package com.sidework.credit.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.credit.application.adapter.CreditController;
import com.sidework.credit.application.port.in.CreditQueryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CreditController.class)
@ContextConfiguration(classes = CreditTestApplication.class)
@Import(ExceptionAdvice.class)
public class CreditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreditQueryUseCase creditQueryService;

    private AuthenticatedUserDetails authenticatedUserDetails =
            new AuthenticatedUserDetails(1L, "test@test.com", "테스터", "password");

    @Test
    void 크레딧_조회_요청시_성공하면_200과_잔액을_반환한다() throws Exception {
        Long expectedCredit = 3000L;

        when(creditQueryService.sumAmountByUser(authenticatedUserDetails.getId()))
                .thenReturn(expectedCredit);

        mockMvc.perform(get("/api/v1/credits")
                        .with(user(authenticatedUserDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(3000));

        verify(creditQueryService).sumAmountByUser(authenticatedUserDetails.getId());
    }

    @Test
    void 크레딧_조회_요청시_크레딧이_없으면_0을_반환한다() throws Exception {
        when(creditQueryService.sumAmountByUser(authenticatedUserDetails.getId()))
                .thenReturn(0L);

        mockMvc.perform(get("/api/v1/credits")
                        .with(user(authenticatedUserDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(0));

        verify(creditQueryService).sumAmountByUser(authenticatedUserDetails.getId());
    }

    @Test
    void 크레딧_조회_요청시_인증되지_않은_사용자면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/credits"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}