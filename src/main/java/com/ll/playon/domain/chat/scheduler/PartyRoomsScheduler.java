package com.ll.playon.domain.chat.scheduler;

import com.ll.playon.domain.chat.event.ChatRoomDetectedAfterGameStartedEvent;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PartyRoomsScheduler {
    private final PartyRoomRepository partyRoomRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void deleteUnusedPartyRooms() {
        LocalDateTime deadlineTime = LocalDateTime.now().minusMinutes(5);
        List<Long> candidates = this.partyRoomRepository.findDeletablePartyRooms(deadlineTime);

        if (!candidates.isEmpty()) {
            this.eventPublisher.publishEvent(new ChatRoomDetectedAfterGameStartedEvent(candidates));
        }
    }
}
