package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.image.repository.ImageRepository;
import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.dto.response.GetCompletedPartyDto;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.standard.time.dto.TotalPlayTimeDto;

import java.util.List;
import java.util.Map;

public record GameDetailWithPartyResponse(
        GameDetailResponse game,
        List<GetPartyResponse> partyList,
        List<GetCompletedPartyDto> completedPartyList
) {
    public static GameDetailWithPartyResponse from(
            SteamGame game,
            List<Party> parties,
            List<Party> completedParties,
            Map<Long, PartyMember> mvpMap,
            Map<Long, TotalPlayTimeDto> playTimeMap,
            Map<Long, List<PartyDetailMemberDto>> memberMap,
            Map<Long, List<PartyDetailTagDto>> tagMap
    ) {
        return new GameDetailWithPartyResponse(
                GameDetailResponse.from(game),
                parties.stream()
                        .map(party -> new GetPartyResponse(
                                party,
                                party.getPartyTags(),
                                party.getPartyMembers()
                        ))
                        .toList(),
                completedParties.stream()
                        .map(party -> new GetCompletedPartyDto(
                                party,
                                mvpMap.get(party.getId()),
                                playTimeMap.get(party.getId()),
                                memberMap.get(party.getId()),
                                tagMap.get(party.getId())
                        ))
                        .toList()
        );
    }
}