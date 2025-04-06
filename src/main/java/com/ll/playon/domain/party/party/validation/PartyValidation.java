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
}
