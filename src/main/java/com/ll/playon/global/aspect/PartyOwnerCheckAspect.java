package com.ll.playon.global.aspect;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.entity.Party;
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
public class PartyOwnerCheckAspect {
    private final PartyRepository partyRepository;

    @Around("@annotation(com.ll.playon.global.annotation.PartyOwnerOnly)")
    public Object checkPartyOwner(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        Member actor = (Member) args[0];
        Long partyId = (Long) args[1];
        Party party = this.partyRepository.findById(partyId)
                .orElseThrow(ErrorCode.PARTY_NOT_FOUND::throwServiceException);

        if (isNotPartyOwner(actor, party)) {
            ErrorCode.IS_NOT_PARTY_MEMBER_OWNER.throwServiceException();
        }

        PartyContext.setParty(party);

        try {
            return joinPoint.proceed(args);
        } finally {
            PartyContext.clear();
        }
    }

    private boolean isNotPartyOwner(Member actor, Party party) {
        return party.getPartyMembers().stream()
                .noneMatch(pm -> pm.getPartyRole().equals(PartyRole.OWNER)
                                && pm.getMember().getId().equals(actor.getId()));
    }
}
