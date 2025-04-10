package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.entity.Party;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;

public record GetPartyDetailResponse(
        long partyId,

        @NotBlank
        String name,

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
    public GetPartyDetailResponse(Party party, List<PartyDetailMemberDto> partyMembers,
                                  List<PartyDetailTagDto> partyTags) {
        this(
                party.getId(),
                party.getName(),
                party.getDescription(),
                party.getPartyAt(),
                party.getHit(),
                partyMembers,
                partyTags
        );
    }
}
