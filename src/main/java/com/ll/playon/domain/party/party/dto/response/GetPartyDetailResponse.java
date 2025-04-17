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

        long appId,

        @NotBlank
        String name,

        @NonNull
        String description,

        @NonNull
        LocalDateTime partyAt,

        long hit,

        long ownerId,

        @NonNull
        List<PartyDetailMemberDto> partyMembers,

        @NonNull
        List<PartyDetailTagDto> partyTags
) {
    public GetPartyDetailResponse(Party party, PartyMember owner, List<PartyDetailMemberDto> partyMembers,
                                  List<PartyDetailTagDto> partyTags) {
        this(
                party.getId(),
                party.getGame().getAppid(),
                party.getName(),
                party.getDescription(),
                party.getPartyAt(),
                party.getHit(),
                owner.getMember().getId(),
                partyMembers,
                partyTags
        );
    }
}
