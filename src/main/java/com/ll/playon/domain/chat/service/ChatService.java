package com.ll.playon.domain.chat.service;

import com.ll.playon.domain.chat.context.ChatMemberContext;
import com.ll.playon.domain.chat.context.PartyRoomContext;
import com.ll.playon.domain.chat.dto.ChatMemberDto;
import com.ll.playon.domain.chat.dto.GetChatRoomResponse;
import com.ll.playon.domain.chat.entity.ChatMember;
import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.chat.mapper.ChatMemberMapper;
import com.ll.playon.domain.chat.policy.PartyRoomPolicy;
import com.ll.playon.domain.chat.repository.ChatMemberRepository;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import com.ll.playon.domain.chat.validation.PartyRoomValidation;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.context.PartyMemberContext;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.type.PartyStatus;
import com.ll.playon.domain.title.service.MemberTitleService;
import com.ll.playon.global.annotation.ActivePartyMemberOnly;
import com.ll.playon.global.annotation.ChatMemberOnly;
import com.ll.playon.global.exceptions.ErrorCode;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageService chatMessageService;
    private final MemberTitleService memberTitleService;
    private final ChatMemberRepository chatMemberRepository;
    private final PartyRoomRepository partyRoomRepository;

    // 채팅방 입장
    @ActivePartyMemberOnly
    @Transactional
    public GetChatRoomResponse enterPartyRoom(Member actor, Long partyId) {
        Party party = PartyContext.getParty();

        PartyRoomValidation.checkPartyRoomCanEnter(party);

        if (party.getPartyStatus().equals(PartyStatus.PENDING)) {
            party.updatePartyStatus(PartyStatus.ONGOING);
        }

        PartyMember partyMember = PartyMemberContext.getPartyMember();
        PartyRoom partyRoom = this.getPartyRoom(party);

        // 중복 참여 시 바로 입장
        if (this.isAlreadyEntered(partyRoom, partyMember)) {
            return new GetChatRoomResponse(
                    partyRoom.getId(),
                    partyId,
                    this.getChatMemberDtos(partyRoom),
                    Collections.emptyList()
            );
        }

        this.chatMemberRepository.save(ChatMemberMapper.of(partyRoom, partyMember));

        List<ChatMemberDto> chatMemberDtos = this.getChatMemberDtos(partyRoom);

        String title = this.memberTitleService.getRepresentativeTitle(actor);

        this.chatMessageService.broadcastEnterMessage(partyId, actor, title);

        this.chatMessageService.broadcastMemberList(partyId, chatMemberDtos);

        return new GetChatRoomResponse(
                partyRoom.getId(),
                partyId,
                chatMemberDtos,
                Collections.emptyList()
        );
    }

    // 채팅방 퇴장
    @ChatMemberOnly
    @Transactional
    public void leavePartyRoom(Member actor, long partyId) {
        Party party = PartyContext.getParty();
        PartyRoom partyRoom = PartyRoomContext.getPartyRoom();
        ChatMember chatMember = ChatMemberContext.getChatMember();

        this.chatMemberRepository.delete(chatMember);

        long remainCount = this.chatMemberRepository.countByPartyRoom(partyRoom);

        // 파티 진행 시간 5분 이상 지나고, 채팅인원이 0명이면 채팅방 삭제, 브로드캐스트 생략
        if (PartyRoomPolicy.shouldDeletePartyRoom(remainCount, party)) {
            this.partyRoomRepository.delete(partyRoom);
            party.updatePartyStatus(PartyStatus.COMPLETED);
            party.updateEndTime();
            return;
        }

        String title = this.memberTitleService.getRepresentativeTitle(actor);

        this.chatMessageService.broadcastLeaveMessage(partyId, actor, title);

        this.chatMessageService.broadcastMemberList(partyId, this.getChatMemberDtos(partyRoom));
    }

    // Party로 PartyRoom 조회
    private PartyRoom getPartyRoom(Party party) {
        return this.partyRoomRepository.findByParty(party)
                .orElseThrow(ErrorCode.PARTY_ROOM_NOT_FOUND::throwServiceException);
    }

    // PartyRoom으로 채팅방 참여 멤버(DTO) 리스트 조회
    private List<ChatMemberDto> getChatMemberDtos(PartyRoom partyRoom) {
        return this.chatMemberRepository.findAllByPartyRoom(partyRoom).stream()
                .map(ChatMemberDto::new)
                .toList();
    }

    // 이미 채팅방에 입장되어 있는지 확인
    private boolean isAlreadyEntered(PartyRoom partyRoom, PartyMember partyMember) {
        return this.chatMemberRepository.existsByPartyRoomAndPartyMember(partyRoom, partyMember);
    }
}
