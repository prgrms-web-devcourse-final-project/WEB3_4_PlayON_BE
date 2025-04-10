package com.ll.playon.domain.party.party.util;

import static org.springframework.data.domain.Sort.Order;
import static org.springframework.data.domain.Sort.by;

import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;

public class PartySortUtils {
    public static Sort getSort(String orderBy) {
        return switch (orderBy) {
            case "popular" -> by(Order.desc("hit"), Order.asc("partyAt"));
            case "partyAt" -> by(Order.asc("partyAt"), Order.desc("createdAt"));
            case "personal" -> by(Order.desc("createdAt"), Order.asc("partyAt"));
            default -> by(Order.desc("createdAt"), Order.asc("partyAt"));
        };
    }

    public static boolean isPersonalSort(String orderBy) {
        return "personal".equals(orderBy);
    }

    public static Comparator<GetPartyResponse> compare(String orderBy, Map<Long, Party> partyMap,
                                                       Map<Long, Long> totalCountMap) {
        if ("personal".equals(orderBy)) {
            return Comparator
                    .comparing((GetPartyResponse dto) -> totalCountMap.getOrDefault(dto.partyId(), 0L))
                    .reversed()
                    .thenComparing(dto -> partyMap.get(dto.partyId()).getPartyAt());
        }

        return null;
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
