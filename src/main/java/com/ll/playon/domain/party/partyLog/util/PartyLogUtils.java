package com.ll.playon.domain.party.partyLog.util;

import com.ll.playon.domain.party.party.entity.Party;

public class PartyLogUtils {

    // 파티의 파티로그가 있는지 여부
    public static boolean hasPartyLog(Party party) {
        return party.getPartyMembers().stream()
                .anyMatch(pm -> pm.getPartyLog() != null);
    }
}
