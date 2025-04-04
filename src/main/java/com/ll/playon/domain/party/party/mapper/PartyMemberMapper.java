package com.ll.playon.domain.party.party.mapper;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.type.PartyRole;

public class PartyMemberMapper {
    public static PartyMember of(Member actor, PartyRole partyRole) {
        return PartyMember.builder()
                .member(actor)
                .partyRole(partyRole)
                .mvpPoint(0)
                .build();
    }
}
