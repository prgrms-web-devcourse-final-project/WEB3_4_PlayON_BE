package com.ll.playon.domain.party.party.dto;

import com.ll.playon.domain.party.party.entity.PartyMember;
import java.util.Objects;

public record PartyMemberIdProfileImgDto(
        long memberId,

        String profileImg
) {
    public PartyMemberIdProfileImgDto(PartyMember partyMember) {
        this(
                partyMember.getMember().getId(),
                Objects.requireNonNullElse(partyMember.getMember().getProfileImg(), "")
        );
    }
}
