package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.type.PartyRole;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;

public record GetPartyResponse(
        long partyId,

        @NotBlank
        String name,

        @NonNull
        String description,

        @NonNull
        LocalDateTime partyAt,

        int total,

        @NonNull
        List<PartyDetailMemberDto> members,

        @NonNull
        List<PartyDetailTagDto> partyTags

        // TODO: 1. 스팀 게임 헤더 이미지
        //       2. 스팀 아바타로 변경할지 고려
) {
    public GetPartyResponse(Party party, List<PartyTag> tagDtos, List<PartyMember> memberDtos) {
        this(
                party.getId(),
                party.getName(),
                party.getDescription(),
                party.getPartyAt(),
                (int) memberDtos.stream()
                        .filter(pm -> !pm.getPartyRole().equals(PartyRole.PENDING))
                        .count(),
                memberDtos.stream()
                        .map(PartyDetailMemberDto::new)
                        .toList(),
                tagDtos.stream()
                        .map(tag -> tag.getValue().getKoreanValue())
                        .map(PartyDetailTagDto::new)
                        .toList()
        );
    }
}
