package com.ll.playon.domain.chat.event;

import com.ll.playon.domain.chat.entity.PartyRoom;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PartyRoomExpiredEvent(
        @NotNull
        List<PartyRoom> candidatePartyRooms
) {
}
