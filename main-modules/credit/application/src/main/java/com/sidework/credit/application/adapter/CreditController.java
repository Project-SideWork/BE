package com.sidework.credit.application.adapter;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.credit.application.port.in.CreditQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credits")
@RequiredArgsConstructor
@Slf4j
public class CreditController {
    private final CreditQueryUseCase creditQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Integer>> getCredit(@AuthenticationPrincipal AuthenticatedUserDetails user) {
        return ResponseEntity.ok(ApiResponse.onSuccess(creditQueryService.sumAmountByUser(user.getId())));
    }
}
