package com.ll.playon.global.aspect;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.context.PartyMemberContext;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.repository.PartyMemberRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PartyInviterCheckAspect {
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;

    @Around("@annotation(com.ll.playon.global.annotation.PartyInviterOnly)")
    public Object checkPartyInviter(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        Member actor = (Member) args[0];
        Long partyId = (Long) args[1];
        Party party = this.partyRepository.findById(partyId)
                .orElseThrow(ErrorCode.PARTY_NOT_FOUND::throwServiceException);

        if (isNotPartyInviter(actor, party)) {
            throw ErrorCode.IS_NOT_PARTY_MEMBER_INVITER.throwServiceException();
        }

        PartyMember partyMember = this.partyMemberRepository.findByMemberAndParty(actor, party)
                .orElseThrow(ErrorCode.PARTY_MEMBER_NOT_FOUND::throwServiceException);

        PartyContext.setParty(party);
        PartyMemberContext.setPartyMember(partyMember);

        try {
            return joinPoint.proceed(args);
        } finally {
            PartyContext.clear();
            PartyMemberContext.clear();
        }
    }

    private boolean isNotPartyInviter(Member actor, Party party) {
        return party.getPartyMembers().stream()
                .noneMatch(pm -> pm.getPartyRole().equals(PartyRole.INVITER)
                                && pm.getMember().getId().equals(actor.getId()));
    }
}

