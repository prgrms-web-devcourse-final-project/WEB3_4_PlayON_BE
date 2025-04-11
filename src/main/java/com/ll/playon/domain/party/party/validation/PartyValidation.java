package com.ll.playon.domain.party.party.validation;

import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.type.PartyStatus;
import com.ll.playon.global.exceptions.ErrorCode;

public class PartyValidation {

    // 파티가 끝났는지 확인
    public static void checkIsPartyClosed(Party party) {
        if (!party.getPartyStatus().equals(PartyStatus.COMPLETED)) {
            ErrorCode.PARTY_IS_NOT_ENDED.throwServiceException();
        }
    }

    // 파티가 참가할 수 있는 상태인지 확인
    public static void checkPartyCanJoin(Party party) {
        if (!party.getPartyStatus().equals(PartyStatus.PENDING)) {
            ErrorCode.IS_NOT_PARTY_PENDING.throwServiceException();
        }
    }
}
