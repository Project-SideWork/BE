package com.sidework.payment.application.service;

import com.sidework.payment.application.port.in.CreditQueryUseCase;
import com.sidework.payment.application.port.out.CreditOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditQueryService implements CreditQueryUseCase {
    private final CreditOutPort creditRepository;


    @Override
    public Long sumAmountByUser(Long userId) {
        return creditRepository.findAmountByUser(userId);
    }
}
