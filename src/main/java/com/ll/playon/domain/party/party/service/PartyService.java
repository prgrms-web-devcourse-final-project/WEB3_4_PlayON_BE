package com.ll.playon.domain.party.party.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PutPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.mapper.PartyMapper;
import com.ll.playon.domain.party.party.mapper.PartyMemberMapper;
import com.ll.playon.domain.party.party.repository.PartyMemberRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyTagService partyTagService;
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;

    // 파티 생성
    @Transactional
    public PostPartyResponse createParty(Member actor, PostPartyRequest request) {
        Party party = PartyMapper.of(request);

        List<PartyTag> partyTags = this.createPartyTag(request, party);
        party.setPartyTags(partyTags);

        PartyMember partyMember = PartyMemberMapper.of(actor, PartyRole.OWNER);

        partyMember.setParty(party);
        party.getPartyMember().add(partyMember);

        // TODO: 1. Game 헤더 이미지 응답
        //       2. 파티룸 생성

        return new PostPartyResponse(this.partyRepository.save(party));
    }

    // 파티 수정
    @Transactional
    public PutPartyResponse updateParty(Member actor, long playId, PutPartyRequest putPartyRequest) {
        Party party = getParty(playId);
        PartyMember partyMember = this.getPartyMember(actor, party);

        if (!this.isPartyOwner(partyMember)) {
            ErrorCode.INVALID_PARTY_MEMBER.throwServiceException();
        }

        // TODO: 유니크 제약 조건 있으면 체크

        this.updatePartyFromRequest(party, putPartyRequest);

        this.partyTagService.updatePartyTags(party, putPartyRequest.tags());

        return new PutPartyResponse(party);
    }

    // 파티 ID로 파티 조회
    private Party getParty(long playId) {
        return this.partyRepository.findById(playId)
                .orElseThrow(ErrorCode.PARTY_NOT_FOUND::throwServiceException);
    }

    // Member, PartyId로 파티 멤버 조회
    private PartyMember getPartyMember(Member actor, Party party) {
        return this.partyMemberRepository.findByMemberAndParty(actor, party)
                .orElseThrow(ErrorCode.PARTY_MEMBER_NOT_FOUND::throwServiceException);
    }

    // 요청으로부터 파티 태그 리스트 생성
    private List<PartyTag> createPartyTag(PostPartyRequest request, Party party) {
        return request.tags().stream()
                .map(tag -> PartyTag.builder()
                        .party(party)
                        .type(TagType.fromValue(tag.type()))
                        .value(TagValue.fromValue(tag.value()))
                        .build())
                .toList();
    }

    // 파티 생성자 확인
    private boolean isPartyOwner(PartyMember partyMember) {
        return partyMember.getPartyRole().equals(PartyRole.OWNER);
    }

    // 파티 정보 수정
    private void updatePartyFromRequest(Party party, PutPartyRequest request) {
        party.setName(request.name());
        party.setDescription(request.description() != null ? request.description() : "");
        party.setPartyAt(request.partyAt());
        party.setPublic(request.isPublic());
        party.setMinimum(request.minimum());
        party.setMaximum(request.maximum());
        party.setGame(request.game());
    }
}
