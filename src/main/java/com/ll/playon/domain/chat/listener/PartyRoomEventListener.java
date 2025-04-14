package com.ll.playon.domain.chat.listener;

import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.chat.event.PartyRoomExpiredEvent;
import com.ll.playon.domain.chat.event.PartyRoomUnusedEvent;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import com.ll.playon.domain.chat.service.ChatService;
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
public class PartyRoomEventListener {
    private final ChatService chatService;
    private final PartyRoomRepository partyRoomRepository;

    @EventListener
    public void handleUnused(PartyRoomUnusedEvent event) {
        int successCount = 0;
        List<Long> failedIds = new ArrayList<>();

        List<Long> candidateIds = event.candidateIds();

        for (Long partyRoomId : candidateIds) {
            try {
                this.partyRoomRepository.deleteById(partyRoomId);
                successCount++;
            } catch (Exception ex) {
                failedIds.add(partyRoomId);
                log.error("id={}번 파티룸 삭제 실패", partyRoomId, ex);
            }
        }

        if (successCount == 0) {
            throw new EventListenerException(ErrorCode.PARTY_ROOM_DELETE_FAILED);
        }

        log.info("삭제된 파티룸: {}개, 삭제 실패: {}개, 실패 파티룸 ID: {}", successCount, failedIds.size(), failedIds);
    }

    @EventListener
    public void handleExpired(PartyRoomExpiredEvent event) {
        int successCount = 0;
        List<Long> failedIds = new ArrayList<>();

        List<PartyRoom> expiredPartyRooms = event.candidatePartyRooms();

        for (PartyRoom partyRoom : expiredPartyRooms) {
            try {
                this.chatService.deletePartyRoomByHard(partyRoom);
                successCount++;
            } catch (Exception ex) {
                failedIds.add(partyRoom.getId());
                log.error("id={}번 파티룸 삭제 실패", partyRoom.getId(), ex);
            }
        }

        if (successCount == 0) {
            throw new EventListenerException(ErrorCode.PARTY_ROOM_DELETE_FAILED);
        }

        log.info("삭제된 파티룸: {}개, 삭제 실패: {}개, 실패 파티룸 ID: {}", successCount, failedIds.size(), failedIds);
    }
}
