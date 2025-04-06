package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.party.party.entity.Party;

import java.time.LocalDateTime;
import java.util.List;

public record PartySummaryResponse(
        Long id,
        String name,
        LocalDateTime partyAt,
        List<String> tags,
        int memberCount
) {
    public static PartySummaryResponse from(Party party) {
        return new PartySummaryResponse(
                party.getId(),
                party.getName(),
                party.getPartyAt(),
                party.getPartyTags().stream()
                        .map(t -> t.getValue().getKoreanValue())
                        .toList(),
                party.getPartyMembers().size()
        );
    }
}