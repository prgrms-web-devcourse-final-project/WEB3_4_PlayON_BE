package com.ll.playon.domain.chat.listener;

import com.ll.playon.domain.chat.dto.ChatMemberCountDto;
import com.ll.playon.domain.chat.event.ChatRoomDetectedAfterGameStartedEvent;
import com.ll.playon.domain.chat.policy.PartyRoomPolicy;
import com.ll.playon.domain.chat.repository.ChatMemberRepository;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.exceptions.EventListenerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PartyRoomEventListener {
    private final ChatMemberRepository chatMemberRepository;
    private final PartyRoomRepository partyRoomRepository;

    @EventListener
    public void handle(ChatRoomDetectedAfterGameStartedEvent event) {
        int successCount = 0;
        List<Long> failedIds = new ArrayList<>();

        List<Long> candidateIds = event.candidateIds();

        Map<Long, Long> remainCountMap = this.chatMemberRepository.countByPartyRoomIds(candidateIds).stream()
                .collect(Collectors.toMap(ChatMemberCountDto::partyRoomId, ChatMemberCountDto::remainCount));

        for (Long partyRoomId : candidateIds) {
            Long remainCount = remainCountMap.getOrDefault(partyRoomId, 0L);

            try {
                if (PartyRoomPolicy.shouldDeletePartyRoom(remainCount)) {
                    this.partyRoomRepository.deleteById(partyRoomId);
                    successCount++;
                }
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
}
