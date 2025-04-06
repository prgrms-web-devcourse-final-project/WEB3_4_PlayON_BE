package com.ll.playon.domain.party.party.util;

import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PartySortUtils {
    public static Comparator<GetPartyResponse> compare(String orderBy, Map<Long, Party> partyMap, Map<Long, Long> totalCountMap) {
        return switch (orderBy) {
            case "popular" -> Comparator.comparing((GetPartyResponse dto) -> partyMap.get(dto.partyId()).getHit()).reversed();
            case "partyAt" -> Comparator.comparing((GetPartyResponse dto) -> partyMap.get(dto.partyId()).getPartyAt());
            case "personal" -> Comparator.comparing((GetPartyResponse dto) -> totalCountMap.get(dto.partyId())).reversed();
            default -> Comparator.comparing((GetPartyResponse dto) -> partyMap.get(dto.partyId()).getCreatedAt()).reversed();
        };
    }

    public static Map<Long, Party> convertToMap(List<Party> parties) {
        return parties.stream()
                .collect(Collectors.toMap(Party::getId, Function.identity()));
    }

    public static Map<Long, Long> convertToTotalCountMap(List<PartyMember> partyMembers) {
        return partyMembers.stream()
                .collect(Collectors.groupingBy(pm -> pm.getParty().getId(), Collectors.counting()));
    }

}
