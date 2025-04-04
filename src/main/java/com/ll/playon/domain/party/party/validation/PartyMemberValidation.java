package com.ll.playon.domain.party.party.validation;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.global.exceptions.ErrorCode;

public class PartyMemberValidation {

    // 해당 파티멤버가 본인 자신인지 확인
    public static void checkIsPartyMemberOwn(PartyMember partyMember, Member actor) {
        if (partyMember.isOwn(actor)) {
            ErrorCode.IS_PARTY_MEMBER_OWN.throwServiceException();
        }
    }

    // 이미 파티에 신청했는지 확인
    public static void checkPendingMember(PartyMember partyMember) {
        if (partyMember.getPartyRole().equals(PartyRole.PENDING)) {
            ErrorCode.IS_ALREADY_REQUEST_PARTY.throwServiceException();
        }
    }
}
