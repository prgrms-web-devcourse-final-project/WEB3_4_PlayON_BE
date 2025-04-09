package com.ll.playon.domain.chat.event;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChatRoomDetectedAfterGameStartedEvent(
        @NotNull
        List<Long> candidateIds
) {
}
