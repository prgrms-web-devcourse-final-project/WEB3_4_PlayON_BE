package com.ll.playon.global.aspect;

import com.ll.playon.domain.chat.context.ChatMemberContext;
import com.ll.playon.domain.chat.context.PartyRoomContext;
import com.ll.playon.domain.chat.entity.ChatMember;
import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.chat.repository.ChatMemberRepository;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.repository.PartyMemberRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ChatMemberCheckAspect {
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final PartyRoomRepository partyRoomRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Around("@annotation(com.ll.playon.global.annotation.ChatMemberOnly)")
    public Object checkPartyMember(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        Member actor = (Member) args[0];
        Long partyId = (Long) args[1];
        Party party = this.partyRepository.findById(partyId)
                .orElseThrow(ErrorCode.PARTY_NOT_FOUND::throwServiceException);

        PartyRoom partyRoom = this.partyRoomRepository.findByParty(party)
                .orElseThrow(ErrorCode.PARTY_ROOM_NOT_FOUND::throwServiceException);

        if (isNotActivePartyMember(actor, party)) {
            ErrorCode.IS_NOT_PARTY_MEMBER_MEMBER.throwServiceException();
        }

        PartyMember partyMember = this.partyMemberRepository.findByMemberAndParty(actor, party)
                .orElseThrow(ErrorCode.PARTY_MEMBER_NOT_FOUND::throwServiceException);

        ChatMember chatMember = this.chatMemberRepository.findByPartyRoomAndPartyMember(partyRoom, partyMember)
                .orElseThrow(ErrorCode.IS_NOT_CHAT_MEMBER::throwServiceException);

        PartyContext.setParty(party);
        PartyRoomContext.setPartyRoom(partyRoom);
        ChatMemberContext.setChatMember(chatMember);

        try {
            return joinPoint.proceed(args);
        } finally {
            PartyContext.clear();
            PartyRoomContext.clear();
            ChatMemberContext.clear();
        }
    }

    // 활성화된 파티 멤버인지 확인
    private boolean isNotActivePartyMember(Member actor, Party party) {
        return party.getPartyMembers().stream()
                .noneMatch(pm -> pm.getMember().getId().equals(actor.getId())
                                 && pm.getPartyRole().isActive());
    }
}
