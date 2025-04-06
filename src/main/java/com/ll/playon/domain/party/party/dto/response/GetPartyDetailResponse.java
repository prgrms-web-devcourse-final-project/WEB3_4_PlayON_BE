package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;

public record GetPartyDetailResponse(
        long partyId,

        @NotBlank
        String name,

        @NotBlank
        String ownerName,

        // TODO: 스팀 아바타로 변경할지 고려
        @NonNull
        String ownerProfileImg,

        @NonNull
        String description,

        @NonNull
        LocalDateTime partyAt,

        long hit,

        @NonNull
        List<PartyDetailMemberDto> partyMembers,

        @NonNull
        List<PartyDetailTagDto> partyTags

        // TODO: 채팅 룸 여기에?
) {
    public GetPartyDetailResponse(Party party, PartyMember owner, List<PartyDetailMemberDto> partyMembers,
                                  List<PartyDetailTagDto> partyTags) {
        this(
                party.getId(),
                party.getName(),
                owner.getMember().getNickname(),
                owner.getMember().getProfileImg(),
                party.getDescription(),
                party.getPartyAt(),
                party.getHit(),
                partyMembers,
                partyTags
        );
    }
}
