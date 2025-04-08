package com.ll.playon.domain.chat.scheduled;

import com.ll.playon.domain.chat.event.PartyRoomsDeleteEvent;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletePartyRoomsScheduler {
    private final PartyRoomRepository partyRoomRepository;
    private final ApplicationEventPublisher eventPublisher;

    // TODO: 배포환경에서 주석 해제
//    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void deleteUnusedPartyRooms() {
        LocalDateTime deadlineTime = LocalDateTime.now().minusMinutes(5);
        List<Long> candidates = this.partyRoomRepository.findDeletablePartyRooms(deadlineTime);

        if (!candidates.isEmpty()) {
            this.eventPublisher.publishEvent(new PartyRoomsDeleteEvent(candidates));
        }
    }
}
