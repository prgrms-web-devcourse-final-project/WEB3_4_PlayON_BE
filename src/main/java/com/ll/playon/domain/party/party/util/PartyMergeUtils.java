package com.ll.playon.domain.party.party.util;

import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartyMergeUtils {

    // Party 내부 Join 데이터들 병합
    public static List<GetPartyResponse> mergePartyWithJoinData(List<Party> parties, List<PartyTag> partyTags,
                                                          List<PartyMember> partyMembers) {
        Map<Long, List<PartyTag>> partyTagsMap = partyTags.stream()
                .collect(Collectors.groupingBy(pt -> pt.getParty().getId()));

        Map<Long, List<PartyMember>> partyMembersMap = partyMembers.stream()
                .collect(Collectors.groupingBy(pm -> pm.getParty().getId()));

        return parties.stream()
                .map(party -> new GetPartyResponse(
                        party,
                        partyTagsMap.getOrDefault(party.getId(), Collections.emptyList()),
                        partyMembersMap.getOrDefault(party.getId(), Collections.emptyList())
                )).collect(Collectors.toList());
    }
}
