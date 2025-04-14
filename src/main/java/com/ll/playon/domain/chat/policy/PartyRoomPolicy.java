package com.ll.playon.domain.chat.policy;

import com.ll.playon.domain.party.party.entity.Party;
import java.time.LocalDateTime;

public class PartyRoomPolicy {
    public static boolean shouldDeletePartyRoom(long count, Party party) {
        return count == 0 && party.getPartyAt().plusMinutes(5).isBefore(LocalDateTime.now());
    }

    public static boolean shouldDeletePartyRoom(long count) {
        return count == 0;
    }

    public static boolean canEnterPartyRoom(Party party) {
        return LocalDateTime.now().isAfter(party.getPartyAt().minusMinutes(5));
    }
}
