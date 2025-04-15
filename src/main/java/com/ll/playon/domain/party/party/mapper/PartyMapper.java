package com.ll.playon.domain.party.party.mapper;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.entity.Party;

public class PartyMapper {
    public static Party of(PostPartyRequest postPartyRequest, SteamGame game) {
        return Party.builder()
                .game(game)
                .name(postPartyRequest.name())
                .description(postPartyRequest.description())
                .partyAt(postPartyRequest.partyAt())
                .publicFlag(postPartyRequest.isPublic())
                .minimum(postPartyRequest.minimum())
                .maximum(postPartyRequest.maximum())
                .build();
    }
}
