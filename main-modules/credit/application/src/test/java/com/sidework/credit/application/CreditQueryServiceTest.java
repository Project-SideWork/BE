package com.sidework.credit.application;

import com.sidework.credit.application.port.out.CreditOutPort;
import com.sidework.credit.application.service.CreditQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditQueryServiceTest {

    @Mock
    private CreditOutPort creditRepository;

    @InjectMocks
    private CreditQueryService service;

    @Test
    void sumAmountByUser는_userId로_크레딧_합계를_반환한다() {
        Long userId = 1L;
        Integer expected = 3000;

        when(creditRepository.findAmountByUser(userId)).thenReturn(expected);

        Integer result = service.sumAmountByUser(userId);

        assertEquals(expected, result);
        verify(creditRepository).findAmountByUser(userId);
    }

    @Test
    void sumAmountByUser는_크레딧이_없으면_null을_반환한다() {
        Long userId = 1L;

        when(creditRepository.findAmountByUser(userId)).thenReturn(null);

        Integer result = service.sumAmountByUser(userId);

        assertNull(result);
        verify(creditRepository).findAmountByUser(userId);
    }
}