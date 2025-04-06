package com.ll.playon.domain.party.party.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.dto.request.GetAllPartiesRequest;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.dto.response.GetAllPendingMemberResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyDetailResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyMainResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PutPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.mapper.PartyMapper;
import com.ll.playon.domain.party.party.mapper.PartyMemberMapper;
import com.ll.playon.domain.party.party.mapper.PartyTagMapper;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.domain.party.party.type.PartyStatus;
import com.ll.playon.domain.party.party.util.PartySortUtils;
import com.ll.playon.domain.party.party.validation.PartyMemberValidation;
import com.ll.playon.global.annotation.PartyOwnerOnly;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.type.TagValue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyTagService partyTagService;
    private final MemberService memberService;
    private final PartyRepository partyRepository;

    // 파티 생성
    @Transactional
    public PostPartyResponse createParty(Member actor, PostPartyRequest request) {
        Party party = PartyMapper.of(request);

        List<PartyTag> partyTags = this.createPartyTag(request, party);
        party.setPartyTags(partyTags);

        party.addPartyMember(PartyMemberMapper.of(actor, PartyRole.OWNER));

        // TODO: 1. Game 헤더 이미지 응답
        //       2. 파티룸 생성

        return new PostPartyResponse(this.partyRepository.save(party));
    }

    // 파티 검색 및 조회 (조건 반영)
    @Transactional(readOnly = true)
    public Page<GetPartyResponse> getAllFilteredParties(Member actor, int page, int pageSize, String orderBy,
                                                        LocalDateTime partyAt,
                                                        GetAllPartiesRequest request) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, PartySortUtils.getSort(orderBy));

        return actor == null
                ? this.getPublicParties(pageable, partyAt, request)
                : this.getPartiesByLoginUser(actor, pageable, partyAt, request);
    }

    // 공개 파티들 조회
    private Page<GetPartyResponse> getPublicParties(Pageable pageable, LocalDateTime partyAt,
                                                    GetAllPartiesRequest request) {
        return getPartiesByConditions(Collections.emptyList(), pageable, partyAt, request);
    }

    // 내 파티 우선 조회 + 공개 파티들 조회
    private Page<GetPartyResponse> getPartiesByLoginUser(Member actor, Pageable pageable, LocalDateTime partyAt,
                                                         GetAllPartiesRequest request) {
        List<Long> myPartyIds = this.partyRepository.findPartyIdsByMember(actor.getId(), partyAt);

        return getPartiesByConditions(myPartyIds, pageable, partyAt, request);
    }

    // 최종 파티 페이징 리스트 조회
    private Page<GetPartyResponse> getPartiesByConditions(List<Long> excludedIds, Pageable pageable,
                                                          LocalDateTime partyAt, GetAllPartiesRequest request) {
        List<String> tagValues = request.tags().stream()
                .map(tag -> TagValue.fromValue(tag.value()).name())
                .toList();

        long tagSize = tagValues.size();

        // 내 파티 ID 제외 + 공개 파티 ID 조회
        Page<Long> publicPartyIds = this.partyRepository.findPublicPartyIdsExcludingMyParties(excludedIds,
                partyAt, tagValues, tagSize, pageable);

        // 최종 파티 ID 페이징 리스트
        List<Long> mergedPartyIds = new ArrayList<>(excludedIds);
        mergedPartyIds.addAll(publicPartyIds.getContent());

        List<Party> parties = this.partyRepository.findPartiesByIds(mergedPartyIds);
        List<PartyMember> partyMembers = this.partyRepository.findPartyMembersByPartyIds(mergedPartyIds);
        List<PartyTag> partyTags = this.partyRepository.findPartyTagsByPartyIds(mergedPartyIds);

        return new PageImpl<>(
                this.mergePartyWithJoinData(
                        parties, partyTags, partyMembers
                ),
                pageable,
                publicPartyIds.getTotalElements()
        );
    }

    // 파티 메인 조회 (limit 만큼)
    public GetPartyMainResponse getPartyMain(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<Party> parties = this.partyRepository.findAllByPartyStatusOrderByPartyAtDescCreatedAtDesc(
                PartyStatus.COMPLETED,
                pageable);

        if (parties.isEmpty()) {
            return new GetPartyMainResponse(Collections.emptyList());
        }

        List<Long> partyIds = parties.stream().map(Party::getId).toList();

        return new GetPartyMainResponse(
                this.mergePartyWithJoinData(
                        parties,
                        this.partyRepository.findPartyTagsByPartyIds(partyIds),
                        this.partyRepository.findPartyMembersByPartyIds(partyIds)
                )
        );
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
    // AOP에 필요한 파라미터
    @PartyOwnerOnly
    @Transactional
    public PutPartyResponse updateParty(Member actor, long partyId, PutPartyRequest putPartyRequest) {
        Party party = PartyContext.getParty();

        // TODO: 유니크 제약 조건 있으면 체크

        this.updatePartyFromRequest(party, putPartyRequest);

        this.partyTagService.updatePartyTags(party, putPartyRequest.tags());

        return new PutPartyResponse(party);
    }

    // 파티 삭제
    // AOP에 필요한 파라미터
    // TODO: 추후 스케쥴링 처리
    @PartyOwnerOnly
    @Transactional
    public void deleteParty(Member actor, long partyId) {
        Party party = PartyContext.getParty();

        party.setPartyStatus(PartyStatus.COMPLETED);
    }

    // 파티 참가 신청 리스트 조회
    // AOP에 필요한 파라미터
    @PartyOwnerOnly
    @Transactional(readOnly = true)
    public GetAllPendingMemberResponse getPartyPendingMembers(Member actor, long partyId) {
        Party party = PartyContext.getParty();

        return new GetAllPendingMemberResponse(
                party.getPartyMembers().stream()
                        .filter(pm -> pm.getPartyRole().equals(PartyRole.PENDING))
                        .map(PartyDetailMemberDto::new)
                        .toList());
    }

    // 파티 참가 신청
    @Transactional
    public void requestParticipation(Member actor, long partyId) {
        Party party = this.getParty(partyId);
        Optional<PartyMember> opPartyMember = this.getPartyMember(actor, party);

        // 이미 해당 파티의 파티원일 경우
        if (opPartyMember.isPresent()) {
            PartyMember partyMember = opPartyMember.get();

            // 본인일 경우
            PartyMemberValidation.checkIsPartyMemberOwn(partyMember, actor);

            // 이미 해당 파티에 신청한 경우
            PartyMemberValidation.checkPendingMember(partyMember);

            // 파티원일 경우
            ErrorCode.IS_ALREADY_PARTY_MEMBER.throwServiceException();
        }

        party.addPartyMember(PartyMemberMapper.of(actor, PartyRole.PENDING));
    }

    // TODO: 동기화 작업
    // 파티 참가 신청 승인
    // AOP에 필요한 파라미터
    @PartyOwnerOnly
    @Transactional
    public void approveParticipation(Member actor, long partyId, long memberId) {
        Party party = PartyContext.getParty();

        PartyMember pendingMember = this.getPendingMember(memberId, party);

        pendingMember.setPartyRole(PartyRole.MEMBER);
    }

    // 파티 참가 신청 거부
    // AOP에 필요한 파라미터
    @PartyOwnerOnly
    @Transactional
    public void rejectParticipation(Member actor, long partyId, long memberId) {
        Party party = PartyContext.getParty();

        PartyMember pendingMember = this.getPendingMember(memberId, party);

        pendingMember.delete();
    }

    // TODO: 알림 기능 구현 후 수정 예정
    // 파티 초대
    // AOP에 필요한 파라미터
    @PartyOwnerOnly
    @Transactional
    public void inviteParty(Member actor, long partyId, long memberId) {
        Party party = PartyContext.getParty();
        Member invitedActor = this.memberService.findById(memberId)
                .orElseThrow(ErrorCode.USER_NOT_REGISTERED::throwServiceException);

        Optional<PartyMember> opPartyMember = this.getPartyMember(actor, party);

        // 이미 해당 파티의 파티원일 경우
        if (opPartyMember.isPresent()) {
            PartyMember partyMember = opPartyMember.get();

            // 본인일 경우
            PartyMemberValidation.checkIsPartyMemberOwn(partyMember, actor);

            // 이미 해당 파티에 신청한 경우
            PartyMemberValidation.checkPendingMember(partyMember);

            // 파티원일 경우
            ErrorCode.IS_ALREADY_PARTY_MEMBER.throwServiceException();
        }

        party.addPartyMember(PartyMemberMapper.of(invitedActor, PartyRole.PENDING));

        // TODO: 알람?
    }

    // 파티 ID로 파티 조회
    public Party getParty(long partyId) {
        return this.partyRepository.findById(partyId)
                .orElseThrow(ErrorCode.PARTY_NOT_FOUND::throwServiceException);
    }

    // 사용자, 파티 정보로 파티 멤버 조회
    public Optional<PartyMember> getPartyMember(Member actor, Party party) {
        return party.getPartyMembers().stream()
                .filter(pm -> Objects.equals(pm.getMember().getId(), actor.getId()))
                .findFirst();
    }

    // Party에서 파티장 조회
    private PartyMember getPartyOwner(Party party) {
        return party.getPartyMembers().stream()
                .filter(pm -> pm.getPartyRole().equals(PartyRole.OWNER))
                .findFirst()
                .orElseThrow(ErrorCode.PARTY_OWNER_NOT_FOUND::throwServiceException);
    }

    // Party 참가 신청자인지 조회
    private PartyMember getPendingMember(long memberId, Party party) {
        return party.getPartyMembers().stream()
                .filter(pm -> pm.getMember().getId() == memberId)
                .filter(pm -> pm.getPartyRole().equals(PartyRole.PENDING))
                .findFirst()
                .orElseThrow(ErrorCode.PENDING_PARTY_MEMBER_NOT_FOUND::throwServiceException);
    }

    // 요청으로부터 파티 태그 리스트 생성
    private List<PartyTag> createPartyTag(PostPartyRequest request, Party party) {
        return request.tags().stream()
                .map(tag -> PartyTagMapper.build(party, tag.type(), tag.value()))
                .toList();
    }

    // 파티 정보 수정
    private void updatePartyFromRequest(Party party, PutPartyRequest request) {
        party.setName(request.name());
        party.setDescription(request.description() != null ? request.description() : "");
        party.setPartyAt(request.partyAt());
        party.setPublicFlag(request.isPublic());
        party.setMinimum(request.minimum());
        party.setMaximum(request.maximum());
        party.setGame(request.game());
    }

    // Party 내부 Join 데이터들 병합
    private List<GetPartyResponse> mergePartyWithJoinData(List<Party> parties, List<PartyTag> partyTags,
                                                          List<PartyMember> partyMembers) {
        Map<Long, List<PartyTag>> partyTagsMap = partyTags.stream()
                .collect(Collectors.groupingBy(pt -> pt.getParty().getId()));

        Map<Long, List<PartyMember>> partyMembersMap = partyMembers.stream()
                .collect(Collectors.groupingBy(pm -> pm.getParty().getId()));

        return parties.stream()
                .map(party -> new GetPartyResponse(
                        party,
                        partyTagsMap.getOrDefault(party.getId(), Collections.emptyList()),
                        partyMembersMap.getOrDefault(party.getId(), Collections.emptyList())
                )).toList();
    }
}
