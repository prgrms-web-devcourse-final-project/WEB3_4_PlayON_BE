package com.ll.playon.domain.party.party.service;

import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.chat.mapper.PartyRoomMapper;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.dto.request.GetAllPartiesRequest;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.dto.response.GetAllPendingMemberResponse;
import com.ll.playon.domain.party.party.dto.response.GetCompletedPartyDto;
import com.ll.playon.domain.party.party.dto.response.GetPartyDetailResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyMainResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResultResponse;
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
import com.ll.playon.domain.party.party.validation.PartyValidation;
import com.ll.playon.domain.party.partyLog.dto.response.GetAllPartyLogResponse;
import com.ll.playon.domain.party.partyLog.service.PartyLogService;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.service.MemberTitleService;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.global.annotation.PartyOwnerOnly;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.type.TagValue;
import com.ll.playon.standard.time.dto.TotalPlayTimeDto;
import com.ll.playon.standard.util.Ut;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyTagService partyTagService;
    private final PartyLogService partyLogService;
    private final MemberService memberService;
    private final MemberTitleService memberTitleService;
    private final PartyRepository partyRepository;
    private final PartyRoomRepository partyRoomRepository;
    private final GameRepository gameRepository;
    private final TitleEvaluator titleEvaluator;

    // 파티 생성
    @Transactional
    public PostPartyResponse createParty(Member actor, PostPartyRequest request) {
        SteamGame game = this.gameRepository.findById(request.gameId())
                .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);

        Party party = PartyMapper.of(request, game);

        List<PartyTag> partyTags = this.createPartyTag(request, party);
        party.setPartyTags(partyTags);

        party.addPartyMember(PartyMemberMapper.of(actor, PartyRole.OWNER));

        PartyRoom partyRoom = PartyRoomMapper.of(party);
        this.partyRoomRepository.save(partyRoom);

        this.partyRepository.save(party);

        // 파티 생성 칭호
        titleEvaluator.check(ConditionType.PARTY_CREATE_COUNT, actor);

        return new PostPartyResponse(party);
    }

    // 파티 검색 및 조회 (조건 반영)
    @Transactional(readOnly = true)
    public Page<GetPartyResponse> getAllFilteredParties(Member actor, int page, int pageSize, String orderBy,
                                                        LocalDateTime partyAt,
                                                        GetAllPartiesRequest request) {
        Sort sort = PartySortUtils.getSort(orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

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

    // TODO: 추후 리팩토링 진행, 성능 개선 필요할 것 같음
    // 최종 파티 페이징 리스트 조회
    private Page<GetPartyResponse> getPartiesByConditions(List<Long> myPartyIds, Pageable pageable,
                                                          LocalDateTime partyAt, GetAllPartiesRequest request) {
        List<String> tagValues = request.tags().stream()
                .map(tag -> TagValue.fromValue(tag.value()).name())
                .toList();

        List<String> genres = request.genres() != null ? request.genres() : Collections.emptyList();

        // 내 파티 ID 제외 + 공개 파티 ID 조회
        Page<Long> partyIds = this.partyRepository.findPartyIdsByAllConditions(
                myPartyIds.isEmpty() ? Collections.singletonList(-1L) : myPartyIds,
                partyAt,
                tagValues,
                tagValues.size(),
                tagValues.isEmpty() ? 1 : 0,
                request.gameId(),
                genres,
                genres.size(),
                genres.isEmpty() ? 1 : 0,
                pageable
        );

        if (partyIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Party> parties = this.partyRepository.findPartiesByIds(partyIds.getContent());
        List<PartyMember> partyMembers = this.partyRepository.findPartyMembersByPartyIds(partyIds.getContent());
        List<PartyTag> partyTags = this.partyRepository.findPartyTagsByPartyIds(partyIds.getContent());

        List<GetPartyResponse> getPartyResponses = this.mergePartyWithJoinData(parties, partyTags, partyMembers);

        List<Long> orderedIds = partyIds.getContent();
        Map<Long, GetPartyResponse> responseMap = getPartyResponses.stream()
                .collect(Collectors.toMap(GetPartyResponse::partyId, Function.identity()));

        List<GetPartyResponse> orderedResponse = new ArrayList<>(orderedIds.stream()
                .map(responseMap::get)
                .filter(Objects::nonNull)
                .toList());

        return new PageImpl<>(orderedResponse, pageable, partyIds.getTotalElements());
    }

    // 파티 결과창 조회
    @Transactional(readOnly = true)
    public GetPartyResultResponse getPartyResult(long partyId) {
        Party party = this.getParty(partyId);

        PartyValidation.checkIsPartyClosed(party);

        List<PartyMember> partyMembers = party.getPartyMembers().stream()
                .filter(pm -> pm.getPartyRole().equals(PartyRole.OWNER) || pm.getPartyRole().equals(PartyRole.MEMBER))
                .toList();

        PartyMember mvp = partyMembers.stream()
                .filter(pm -> pm.getMvpPoint() > 0)
                .max(Comparator.comparingInt(PartyMember::getMvpPoint))
                .orElseGet(() -> this.getPartyOwner(party));

        TotalPlayTimeDto totalPlayTime = Ut.Time.getTotalPlayTime(party.getPartyAt(), party.getEndedAt());

        List<Long> memberIds = partyMembers.stream()
                .map(pm -> pm.getMember().getId())
                .toList();

        Map<Long, String> titleMap = this.memberTitleService.getRepresentativeTitleMap(memberIds);

        List<PartyDetailMemberDto> partyMemberDtos = partyMembers.stream()
                .map(pm -> new PartyDetailMemberDto(pm, titleMap))
                .toList();

        List<PartyDetailTagDto> partyTagDtos = party.getPartyTags().stream()
                .map(PartyDetailTagDto::new)
                .toList();

        GetCompletedPartyDto completedPartyDto = new GetCompletedPartyDto(party, mvp, totalPlayTime, partyMemberDtos,
                partyTagDtos);

        GetAllPartyLogResponse partyLogs = this.partyLogService.getAllPartyLogs(partyId);

        return new GetPartyResultResponse(completedPartyDto, partyLogs);
    }

    // 메인용 진행 예정 파티 조회 (limit 만큼)
    @Transactional(readOnly = true)
    public GetPartyMainResponse getPendingPartyMain(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<Party> parties = this.partyRepository.findAllByPartyStatusAndPublicFlagTrueOrderByPartyAtAscCreatedAtDesc(
                PartyStatus.PENDING,
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

    // 메인용 종료된 파티 조회 (limit 만큼)
    @Transactional(readOnly = true)
    public GetPartyMainResponse getCompletedPartyMain(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<Party> parties = this.partyRepository.findAllByPartyStatusAndPublicFlagTrueOrderByPartyAtDescCreatedAtDesc(
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

    // TODO: 동시성 고려
    // 파티 상세 정보 조회
    @Transactional
    public GetPartyDetailResponse getPartyDetail(long partyId) {
        Party party = this.getParty(partyId);

        List<PartyMember> partyMembers = party.getPartyMembers().stream()
                .filter(pm -> pm.getPartyRole().equals(PartyRole.OWNER) || pm.getPartyRole().equals(PartyRole.MEMBER))
                .toList();

        List<Long> memberIds = partyMembers.stream()
                .map(pm -> pm.getMember().getId())
                .toList();

        Map<Long, String> titleMap = this.memberTitleService.getRepresentativeTitleMap(memberIds);

        List<PartyDetailMemberDto> partyDetailMemberDtos = party.getPartyMembers().stream()
                .map(pm -> new PartyDetailMemberDto(pm, titleMap))
                .toList();

        List<PartyDetailTagDto> partyDetailTagDtos = party.getPartyTags().stream()
                .map(PartyDetailTagDto::new)
                .toList();

        // 조회수 증가
        party.setHit(party.getHit() + 1);

        return new GetPartyDetailResponse(party, partyDetailMemberDtos, partyDetailTagDtos);
    }

    // 파티 수정
    // AOP에 필요한 파라미터
    @PartyOwnerOnly
    @Transactional
    public PutPartyResponse updateParty(Member actor, long partyId, PutPartyRequest putPartyRequest) {
        Party party = PartyContext.getParty();

        SteamGame game = null;
        if (!Objects.equals(party.getGame().getId(), putPartyRequest.gameId())) {
            game = this.gameRepository.findById(putPartyRequest.gameId())
                    .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);
        }

        // TODO: 유니크 제약 조건 있으면 체크
        party.updateParty(putPartyRequest, game);

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

        List<PartyMember> partyMembers = party.getPartyMembers().stream()
                .filter(pm -> pm.getPartyRole().equals(PartyRole.PENDING))
                .toList();

        List<Long> memberIds = partyMembers.stream()
                .map(pm -> pm.getMember().getId())
                .toList();

        Map<Long, String> titleMap = this.memberTitleService.getRepresentativeTitleMap(memberIds);

        return new GetAllPendingMemberResponse(partyMembers.stream()
                .map(pm -> new PartyDetailMemberDto(pm, titleMap))
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

            // 이미 해당 파티에 신청한 경우
            PartyMemberValidation.checkAlreadyPendingMember(partyMember);

            // 파티원일 경우
            ErrorCode.IS_ALREADY_PARTY_MEMBER.throwServiceException();

            // 본인일 경우
            PartyMemberValidation.checkIsPartyMemberOwn(partyMember, actor);
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

        pendingMember.promoteRole(PartyRole.MEMBER);
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

        Optional<PartyMember> opPartyMember = this.getPartyMember(invitedActor, party);

        // 이미 해당 파티의 파티원일 경우
        if (opPartyMember.isPresent()) {
            PartyMember partyMember = opPartyMember.get();

            // 이미 해당 파티에 신청한 경우
            PartyMemberValidation.checkAlreadyInvitedMember(partyMember);

            // 파티원일 경우
            ErrorCode.IS_ALREADY_PARTY_MEMBER.throwServiceException();

            // 본인일 경우
            PartyMemberValidation.checkIsPartyMemberOwn(partyMember, actor);
        }

        party.addPartyMember(PartyMemberMapper.of(invitedActor, PartyRole.INVITER));

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
                )).collect(Collectors.toList());
    }
}
