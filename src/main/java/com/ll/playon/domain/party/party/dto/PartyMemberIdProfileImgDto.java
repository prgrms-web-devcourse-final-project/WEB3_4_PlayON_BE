package com.ll.playon.domain.party.party.dto;

import com.ll.playon.domain.party.party.entity.PartyMember;

public record PartyMemberIdProfileImgDto(
        long memberId,

        String profileImg
) {
    public PartyMemberIdProfileImgDto(PartyMember partyMember) {
        this(
                partyMember.getMember().getId(),
                partyMember.getMember().getProfileImg()
        );
    }
}
