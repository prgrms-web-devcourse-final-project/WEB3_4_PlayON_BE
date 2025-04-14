package com.ll.playon.domain.party.party.listener;

import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.chat.repository.ChatMemberRepository;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.event.ExpiredPartyDetectedEvent;
import com.ll.playon.domain.party.party.repository.PartyRepository;
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
    private final PartyRepository partyRepository;
    private final PartyRoomRepository partyRoomRepository;
    private final ChatMemberRepository chatMemberRepository;

    @EventListener
    public void handle(ExpiredPartyDetectedEvent event) {
        int successCount = 0;
        List<Long> failedIds = new ArrayList<>();

        List<Party> expiredParties = event.candidateIds();

        for (Party party : expiredParties) {
            try {
                PartyRoom partyRoom = this.partyRoomRepository.findByParty(party).orElse(null);

                if (partyRoom != null) {
                    this.chatMemberRepository.deleteAllByPartyRoom(partyRoom);
                    this.partyRoomRepository.delete(partyRoom);
                }

                this.partyRepository.delete(party.deleteCascadeAll());
                successCount++;
            } catch (Exception ex) {
                failedIds.add(party.getId());
                log.error("id={}번 파티 삭제 실패", party.getId(), ex);
            }
        }

        if (successCount == 0) {
            throw new EventListenerException(ErrorCode.PARTY_DELETE_FAILED);
        }

        log.info("삭제된 파티: {}개, 삭제 실패: {}개, 실패 파티 ID: {}", successCount, failedIds.size(), failedIds);
    }
}
