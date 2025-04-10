package com.ll.playon.domain.party.party.dto;

import com.ll.playon.domain.party.party.entity.PartyMember;
import java.util.Map;
import lombok.NonNull;

public record PartyDetailMemberDto(
        long memberId,

        String username,

        String title,

        String nickname,

        @NonNull
        String profileImg
) {
    public PartyDetailMemberDto(PartyMember partyMember, Map<Long, String> titleMap) {
        this(
                partyMember.getMember().getId(),
                partyMember.getMember().getUsername(),
                titleMap.getOrDefault(partyMember.getMember().getId(), ""),
                partyMember.getMember().getNickname(),
                partyMember.getMember().getProfileImg()
        );
    }
}
