package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.dto.PartyMemberIdProfileImgDto;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
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

        long appId,

        @NonNull
        LocalDateTime partyAt,

        int total,

        @NonNull
        List<PartyMemberIdProfileImgDto> members,

        @NonNull
        List<PartyDetailTagDto> partyTags

        // TODO: 스팀 아바타로 변경할지 고려
) {
    public GetPartyResponse(Party party, List<PartyTag> tagDtos, List<PartyMember> memberDtos) {
        this(
                party.getId(),
                party.getName(),
                party.getDescription(),
                party.getGame().getAppid(),
                party.getPartyAt(),
                party.getTotal(),
                memberDtos.stream()
                        .map(PartyMemberIdProfileImgDto::new)
                        .toList(),
                tagDtos.stream()
                        .map(tag -> tag.getValue().getKoreanValue())
                        .map(PartyDetailTagDto::new)
                        .toList()
        );
    }
}
