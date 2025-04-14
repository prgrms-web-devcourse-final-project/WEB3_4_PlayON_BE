package com.ll.playon.domain.party.party.event;

import com.ll.playon.domain.party.party.entity.Party;
import java.util.List;

public record ExpiredPartyDetectedEvent(
        List<Party> candidateIds
) {
}
