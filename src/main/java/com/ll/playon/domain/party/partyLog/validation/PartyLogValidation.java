package com.ll.playon.domain.party.partyLog.validation;

import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.type.PartyStatus;
import com.ll.playon.global.exceptions.ErrorCode;

public class PartyLogValidation {
    // 파티가 종료되었는지 확인
    public static void checkIsPartyEnd(Party party) {
        if (!party.getPartyStatus().equals(PartyStatus.COMPLETED)) {
            ErrorCode.PARTY_IS_NOT_ENDED.throwServiceException();
        }
    }

    // 이미 파티 로그가 작성되었는지 확인
    public static void checkIsPartyLogNotCreated(PartyMember partyMember) {
        if (partyMember.getPartyLog() != null) {
            ErrorCode.PARTY_LOG_ALREADY_EXISTS.throwServiceException();
        }
    }
}
