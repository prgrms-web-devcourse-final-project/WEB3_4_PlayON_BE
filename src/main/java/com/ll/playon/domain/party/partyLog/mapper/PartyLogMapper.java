package com.ll.playon.domain.party.partyLog.mapper;

import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.partyLog.dto.request.PostPartyLogRequest;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;

public class PartyLogMapper {
    public static PartyLog of(PartyMember partyMember, PostPartyLogRequest postPartyLogRequest) {
        return PartyLog.builder()
                .partyMember(partyMember)
                .comment(postPartyLogRequest.comment())
                .content(postPartyLogRequest.content())
                .build();
    }
}
