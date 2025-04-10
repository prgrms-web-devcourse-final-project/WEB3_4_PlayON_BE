package com.ll.playon.domain.chat.validation;

import com.ll.playon.domain.chat.policy.PartyRoomPolicy;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.global.exceptions.ErrorCode;

public class PartyRoomValidation {
    public static void checkPartyRoomCanEnter(Party party) {
        if (!PartyRoomPolicy.canEnterPartyRoom(party)) {
            ErrorCode.PARTY_ROOM_NOT_OPEN.throwServiceException();
        }
    }
}
