package com.ll.playon.domain.party.party.dto;

import com.ll.playon.domain.party.party.entity.PartyMember;
import lombok.NonNull;

public record PartyDetailMemberDto(
        long memberId,

        // TODO: 스팀 아바타로 변경할지 고려
        @NonNull
        String profileImg
) {
    public PartyDetailMemberDto(PartyMember partyMember) {
        this(
                partyMember.getMember().getId(),
                partyMember.getMember().getProfileImg()
        );
    }
}
