package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;

import java.util.List;

public record PartyLogSummaryResponse(
        Long id,
        Long partyId,
        String name,
        int memberCount,
        List<String> tags
) {
    public static PartyLogSummaryResponse from(PartyLog log) {
        Party party = log.getPartyMember().getParty();

        return new PartyLogSummaryResponse(
                log.getId(),
                party.getId(),
                party.getName(),
                party.getPartyMembers().size(),
                party.getPartyTags().stream()
                        .map(tag -> tag.getValue().getKoreanValue())
                        .toList()
        );
    }
}