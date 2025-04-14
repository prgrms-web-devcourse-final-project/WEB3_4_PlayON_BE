package com.ll.playon.domain.chat.scheduler;

import com.ll.playon.domain.chat.dto.RemainMemberCountDto;
import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.chat.event.PartyRoomUnusedEvent;
import com.ll.playon.domain.chat.event.PartyRoomExpiredEvent;
import com.ll.playon.domain.chat.repository.ChatMemberRepository;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomsScheduler {
    private final PartyRoomRepository partyRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void detectAndPublishUnusedPartyRooms() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(5);
        List<Long> startedPartyRoomIds = this.partyRoomRepository.findDeletablePartyRoomIds(deadline);

        if (startedPartyRoomIds.isEmpty()) {
            return;
        }

        Map<Long, Long> remainCountMap = this.chatMemberRepository.countByPartyRoomIds(startedPartyRoomIds).stream()
                .collect(Collectors.toMap(RemainMemberCountDto::partyRoomId, RemainMemberCountDto::remainCount));

        List<Long> candidateIds = startedPartyRoomIds.stream()
                .filter(partyRoomId -> remainCountMap.getOrDefault(partyRoomId, 0L) == 0)
                .toList();

        if (!candidateIds.isEmpty()) {
            this.eventPublisher.publishEvent(new PartyRoomUnusedEvent(candidateIds));
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void detectAndPublishExpiredPartyRooms() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        List<PartyRoom> candidates = this.partyRoomRepository.findDeletablePartyRooms(deadline);

        if (!candidates.isEmpty()) {
            this.eventPublisher.publishEvent(new PartyRoomExpiredEvent(candidates));
        }
    }
}
