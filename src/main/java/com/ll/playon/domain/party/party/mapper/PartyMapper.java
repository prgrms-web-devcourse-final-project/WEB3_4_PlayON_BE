package com.ll.playon.domain.party.party.mapper;

import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.entity.Party;

public class PartyMapper {
    // TODO : Game 엔티티 개설 시 변경
    public static Party of(PostPartyRequest postPartyRequest) {
        return Party.builder()
                .game(postPartyRequest.game())
                .name(postPartyRequest.name())
                .description(postPartyRequest.description())
                .partyAt(postPartyRequest.partyAt())
                .isPublic(postPartyRequest.isPublic())
                .minimum(postPartyRequest.minimum())
                .maximum(postPartyRequest.maximum())
                .build();
    }
}
