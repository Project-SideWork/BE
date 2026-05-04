package com.sidework.profile.application.event;

import com.sidework.common.event.SignUpCompleteEvent;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.domain.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProfileEventListener {
    private final ProfileOutPort profileRepository;

    // TODO: OUTBOX
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSignUpSuccess(SignUpCompleteEvent event){
        Profile profile = Profile.create(event.userId(), null);
        profileRepository.save(profile);
    }
}
