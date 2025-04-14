package com.ll.playon.domain.party.party.scheduler;

import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.event.ExpiredPartyDetectedEvent;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PartyScheduler {
    private final PartyRepository partyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void detectAndPublishExpiredParties() {
        LocalDateTime deadline = LocalDateTime.now().minusMonths(6);
        List<Party> expiredParties = this.partyRepository.findExpiredPartiesToDelete(deadline);

        if (!expiredParties.isEmpty()) {
            this.eventPublisher.publishEvent(new ExpiredPartyDetectedEvent(expiredParties));
        }
    }
}
