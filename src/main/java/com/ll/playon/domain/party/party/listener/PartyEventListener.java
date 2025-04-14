package com.ll.playon.domain.party.party.listener;

import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.event.ExpiredPartyDetectedEvent;
import com.ll.playon.domain.party.party.service.PartyService;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.exceptions.EventListenerException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PartyEventListener {
    private final PartyService partyService;

    @EventListener
    public void handle(ExpiredPartyDetectedEvent event) {
        int successCount = 0;
        List<Long> failedIds = new ArrayList<>();

        List<Party> expiredParties = event.candidateParties();

        for (Party party : expiredParties) {
            try {
                this.partyService.deletePartyByHard(party);
                successCount++;
            } catch (Exception ex) {
                failedIds.add(party.getId());
                log.error("id={}번 파티 삭제 실패", party.getId());
            }
        }

        if (successCount == 0) {
            throw new EventListenerException(ErrorCode.PARTY_DELETE_FAILED);
        }

        log.info("삭제된 파티: {}개, 삭제 실패: {}개, 실패 파티 ID: {}", successCount, failedIds.size(), failedIds);
    }
}
