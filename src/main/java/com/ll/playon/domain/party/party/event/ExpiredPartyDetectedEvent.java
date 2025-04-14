package com.ll.playon.domain.party.party.event;

import com.ll.playon.domain.party.party.entity.Party;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ExpiredPartyDetectedEvent(
        @NotNull
        List<Party> candidateParties
) {
}
