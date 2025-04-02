package com.ll.playon.domain.party.party.validation;

import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.global.exceptions.ErrorCode;

public class PartyMemberValidation {

    // 파티장인지 확인
    public static void checkPartyOwner(PartyMember partyMember) {
        if (partyMember.getPartyRole().equals(PartyRole.OWNER)) {
            ErrorCode.PARTY_OWNER_CANNOT_APPLY.throwServiceException();
        }
    }

    // 이미 파티에 신청했는지 확인
    public static void checkPendingMember(PartyMember partyMember) {
        if (partyMember.getPartyRole().equals(PartyRole.PENDING)) {
            ErrorCode.IS_ALREADY_REQUEST_PARTY.throwServiceException();
        }
    }
}
