package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.standard.time.dto.TotalPlayTimeDto;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;

public record GetCompletedPartyDto(
        long partyId,

        @NotBlank
        String name,

        @NotBlank
        String gameName,

        @NonNull
        String mvpName,

        @NonNull
        String mvpProfileImg,

        @NonNull
        LocalDateTime partyAt,

        @NonNull
        TotalPlayTimeDto playTime,

        @NonNull
        List<PartyDetailMemberDto> partyMembers,

        @NonNull
        List<PartyDetailTagDto> partyTags
) {
    public GetCompletedPartyDto(Party party, PartyMember mvp, TotalPlayTimeDto playTime,
                                List<PartyDetailMemberDto> partyMembers,
                                List<PartyDetailTagDto> partyTags) {
        this(
                party.getId(),
                party.getName(),
                party.getGame().getName(),
                mvp.getMember().getNickname(),
                mvp.getMember().getProfileImg(),
                party.getPartyAt(),
                playTime,
                partyMembers,
                partyTags
        );
    }
}
