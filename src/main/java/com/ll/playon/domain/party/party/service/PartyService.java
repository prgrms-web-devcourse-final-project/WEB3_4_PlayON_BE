package com.ll.playon.domain.party.party.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.dto.response.GetPartyDetailResponse;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PutPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.mapper.PartyMapper;
import com.ll.playon.domain.party.party.mapper.PartyMemberMapper;
import com.ll.playon.domain.party.party.mapper.PartyTagMapper;
import com.ll.playon.domain.party.party.repository.PartyMemberRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.domain.party.party.type.PartyStatus;
import com.ll.playon.global.exceptions.ErrorCode;
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
        party.getPartyMembers().add(partyMember);

        // TODO: 1. Game 헤더 이미지 응답
        //       2. 파티룸 생성

        return new PostPartyResponse(this.partyRepository.save(party));
    }

    // 파티 상세 정보 조회
    @Transactional
    public GetPartyDetailResponse getPartyDetail(long partyId) {
        Party party = this.getParty(partyId);
        PartyMember owner = this.getPartyOwner(party);

        List<PartyDetailMemberDto> partyDetailMemberDtos = party.getPartyMembers().stream()
                .map(PartyDetailMemberDto::new)
                .toList();

        List<PartyDetailTagDto> partyDetailTagDtos = party.getPartyTags().stream()
                .map(PartyDetailTagDto::new)
                .toList();

        // 조회수 증가
        party.setHit(party.getHit() + 1);

        return new GetPartyDetailResponse(party, owner, partyDetailMemberDtos, partyDetailTagDtos);
    }

    // 파티 수정
    @Transactional
    public PutPartyResponse updateParty(Member actor, long partyId, PutPartyRequest putPartyRequest) {
        Party party = this.getParty(partyId);
        PartyMember partyMember = this.getPartyMember(actor, party);

        if (this.isNotPartyOwner(partyMember)) {
            ErrorCode.INVALID_PARTY_MEMBER.throwServiceException();
        }

        // TODO: 유니크 제약 조건 있으면 체크

        this.updatePartyFromRequest(party, putPartyRequest);

        this.partyTagService.updatePartyTags(party, putPartyRequest.tags());

        return new PutPartyResponse(party);
    }

    // 파티 삭제
    // TODO: 추후 스케쥴링 처리
    @Transactional
    public void deleteParty(Member actor, long partyId) {
        Party party = this.getParty(partyId);
        PartyMember partyMember = this.getPartyMember(actor, party);

        if (this.isNotPartyOwner(partyMember)) {
            ErrorCode.INVALID_PARTY_MEMBER.throwServiceException();
        }

        party.setPartyStatus(PartyStatus.COMPLETED);
    }

    // 파티 ID로 파티 조회
    private Party getParty(long partyId) {
        return this.partyRepository.findById(partyId)
                .orElseThrow(ErrorCode.PARTY_NOT_FOUND::throwServiceException);
    }

    // Member, PartyId로 파티 멤버 조회
    private PartyMember getPartyMember(Member actor, Party party) {
        return this.partyMemberRepository.findByMemberAndParty(actor, party)
                .orElseThrow(ErrorCode.PARTY_MEMBER_NOT_FOUND::throwServiceException);
    }

    // Party에서 파티장 조회
    private PartyMember getPartyOwner(Party party) {
        return party.getPartyMembers().stream()
                .filter(pm -> pm.getPartyRole().equals(PartyRole.OWNER))
                .findFirst()
                .orElseThrow(ErrorCode.PARTY_OWNER_NOT_FOUND::throwServiceException);
    }

    // 요청으로부터 파티 태그 리스트 생성
    private List<PartyTag> createPartyTag(PostPartyRequest request, Party party) {
        return request.tags().stream()
                .map(tag -> PartyTagMapper.build(party, tag.type(), tag.value()))
                .toList();
    }

    // 파티 생성자 확인
    private boolean isNotPartyOwner(PartyMember partyMember) {
        return !partyMember.getPartyRole().equals(PartyRole.OWNER);
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
