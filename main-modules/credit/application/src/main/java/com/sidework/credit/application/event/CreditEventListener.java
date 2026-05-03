package com.sidework.credit.application.event;

import com.sidework.common.event.PaymentCompleteEvent;
import com.sidework.credit.application.exception.InsufficientCreditException;
import com.sidework.credit.application.port.out.CreditOutPort;
import com.sidework.credit.domain.Credit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditEventListener {
    private final CreditOutPort repo;

    // TODO: 크레딧 사용 내역 테이블 추가 후 paymentId 사용
    @EventListener
    @Transactional
    public void onPaymentCompleted(PaymentCompleteEvent event) {
        List<Credit> available = repo.findAvailableCredits(event.userId());
        int spent = event.usedCredit();

        for (Credit credit : available) {
            if (spent <= 0) break;

            int deduct = Math.min(credit.getRemainingAmount(), spent);

            credit.spend(deduct);
            spent -= deduct;
        }

        if(spent > 0) throw new InsufficientCreditException();
        repo.saveAll(available);
    }
}
