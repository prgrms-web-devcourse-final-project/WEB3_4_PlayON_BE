package com.ll.playon.domain.member.service;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.game.game.service.GameService;
import com.ll.playon.domain.guild.guild.dto.request.PostImageUrlRequest;
import com.ll.playon.domain.image.event.ImageDeleteEvent;
import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.member.dto.*;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.context.PartyMemberContext;
import com.ll.playon.domain.party.party.dto.response.GetPartyMainResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.domain.party.party.type.PartyStatus;
import com.ll.playon.domain.party.party.util.PartyMergeUtils;
import com.ll.playon.domain.party.party.validation.PartyValidation;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.global.annotation.PartyInviterOnly;
import com.ll.playon.global.annotation.PartyPendingOnly;
import com.ll.playon.global.aws.s3.S3Service;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.steamAPI.SteamAPI;
import com.ll.playon.global.validation.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final UserContext userContext;
    private final SteamAPI steamAPI;
    private final MemberSteamDataRepository memberSteamDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final GameService gameService;
    private final GameRepository gameRepository;
    private final PartyRepository partyRepository;
    private final TitleEvaluator titleEvaluator;
    private final S3Service s3Service;
    private final SteamAsyncService steamAsyncService;
    private final ApplicationEventPublisher publisher;
    private final ImageService imageService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public String genAccessToken(Member user) {
        return authTokenService.genAccessToken(user);
    }

    public Member getUserFromAccessToken(String accessToken) {
        Map<String, Object> payload = authTokenService.payload(accessToken);

        if (ObjectUtils.isEmpty(payload)) {
            return null;
        }

        Member parsedMember = Member.builder()
                .username((String) payload.get("username"))
                .role((Role) payload.get("role"))
                .build();
        parsedMember.changeMemberId(((Number) payload.get("id")).longValue());

        return parsedMember;
    }

    public void steamLogin(Long steamId) {
        Member member = memberRepository.findBySteamId(steamId)
                .orElseThrow(ErrorCode.USER_NOT_REGISTERED::throwServiceException);

        handleSuccessfulLogin(member);
    }

    public void loginNoSteam(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(ErrorCode.USER_NOT_REGISTERED::throwServiceException);

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw ErrorCode.PASSWORD_INCORRECT.throwServiceException();
        }

        // 성공
        handleSuccessfulLogin(member);
    }

    public MemberDetailDto steamSignup(Long steamId) {
        if (memberRepository.findBySteamId(steamId).isPresent()) {
            throw ErrorCode.USER_ALREADY_REGISTERED.throwServiceException();
        }

        Member member = signup(steamId);

        handleSuccessfulLogin(member);

        // 회원가입 칭호
        titleEvaluator.check(ConditionType.REGISTERED, member);

        return new MemberDetailDto(
                member.getNickname(), member.getProfileImg(), member.getPlayStyle(),
                member.getSkillLevel(), member.getGender());
    }

    public MemberDetailDto signupNoSteam(String username, String password) {
        Optional<Member> memberOptional = memberRepository.findByUsername(username);
        if (memberOptional.isPresent()) {
            throw ErrorCode.USER_ALREADY_REGISTERED.throwServiceException();
        }

        Member member = signup(username, password);

        handleSuccessfulLogin(member);

        return new MemberDetailDto(
                member.getNickname(), member.getProfileImg(), member.getPlayStyle(),
                member.getSkillLevel(), member.getGender());
    }

    private Member signup(Long steamId) {
        Map<String, String> profile = steamAPI.getUserProfile(steamId);

        Member newMember = Member.builder()
                .steamId(steamId)
                .username(String.valueOf(steamId))
                .profileImg(profile.get("profileImg"))
                .role(Role.USER)
                .nickname(profile.get("nickname"))
                .build();
        memberRepository.saveAndFlush(newMember);

        publisher.publishEvent(new SignupEvent(newMember));

        return newMember;
    }

    private Member signup(String username, String password) {
        return memberRepository.save(Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .nickname(username)
                .build());
    }

    private void handleSuccessfulLogin(Member member) {
        member.setLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);

        // 쿠키 세팅
        userContext.makeAuthCookies(member);

        // 시큐리티 로그인
        userContext.setLogin(member);
    }

    public void steamLink(Long steamId, Member actor) {
        memberRepository.findById(actor.getId())
                .map(targetMember -> {
                    targetMember.setSteamId(steamId);
                    memberRepository.save(targetMember);

                    steamAsyncService.getUserGamesAndCheckGenres(targetMember);

                    return targetMember;
                })
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);
    }

    @Transactional
    public PresignedUrlResponse modifyMember(PutMemberDetailDto req, Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자 정보 수정 및 저장 (방어적 복사 적용해봄)
        memberRepository.save(member.toBuilder()
                .nickname(req.nickname())
                .playStyle(req.playStyle())
                .skillLevel(req.skillLevel())
                .gender(req.gender())
                .build());

        if (req.updateProfileImg()) {
            // S3 기존 이미지 삭제
            s3Service.deleteObjectByUrl(member.getProfileImg());

            // 수정하는 경우 presigned url 응답
            if (!ObjectUtils.isEmpty(req.newFileType())) {
                FileValidator.validateFileType(req.newFileType());

                return new PresignedUrlResponse(
                        s3Service.generatePresignedUrl(ImageType.MEMBER, member.getId(), req.newFileType())
                                .toString()
                );
            } else {
                // 삭제하는 경우 기존 프로필 이미지도 삭제
                memberRepository.save(member.toBuilder()
                        .profileImg(null)
                        .build());
            }
        }
        return new PresignedUrlResponse(null);
    }

    @Transactional
    public void saveProfileImage(Member actor, PostImageUrlRequest url) {
        if (ObjectUtils.isEmpty(url)) {
            throw ErrorCode.URL_NOT_FOUND.throwServiceException();
        }

        memberRepository.findById(actor.getId()).ifPresent(member ->
                member.changeProfileImg(url.url()));
    }

    @Transactional
    public void deactivateMember(Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자가 소유한 게임 목록 삭제
        memberSteamDataRepository.deleteById(member.getId());

        // 이미지 삭제
        applicationEventPublisher.publishEvent(new ImageDeleteEvent(member.getId(), ImageType.MEMBER));

        // 연결된 길드, 파티, 파티로그 등 남기기 위해서 엔티티를 삭제하지는 않음
        memberRepository.save(member.toBuilder()
                .steamId(null)  // 스팀 연결 해제
                .username("DELETED_" + UUID.randomUUID())
                .nickname("탈퇴한 사용자")
                .profileImg(null)
                .isDeleted(true)
                .build());
    }

    public MemberProfileResponse me(Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자 정보 조회 후 Dto 에 담기
        ProfileMemberDetailDto profileMemberDetailDto = new ProfileMemberDetailDto(
                member.getId(), member.getSteamId(), member.getUsername(), member.getNickname(),
                member.getProfileImg(), member.getLastLoginAt(), member.getPlayStyle(),
                member.getSkillLevel(), member.getGender(), member.getPreferredGenre()
        );

        // 보유한 게임 목록 조회
        List<MemberSteamData> gamesList = memberSteamDataRepository.findAllByMemberId(actor.getId());

        // 모든 정보 MemberProfileResponse 에 담기
        return new MemberProfileResponse(profileMemberDetailDto, getMemberOwnedGamesDto(gamesList));
    }

    private List<GameListResponse> getMemberOwnedGamesDto(List<MemberSteamData> gamesList) {
        final List<SteamGame> gameList = gameRepository.findAllByAppidIn(
                gamesList.stream().map(MemberSteamData::getAppId).toList()); // DB 에 없는 게임은 제외됨
        return gameService.makeGameListWithoutGenre(gameList);
    }

    public List<GetMembersResponse> findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).stream()
                .map(member ->
                        new GetMembersResponse(member.getId(), member.getUsername(), member.getProfileImg()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GameListResponse> getOwnedGamesByMember(int count, Member actor) {

        List<Long> ownedGames = memberSteamDataRepository.findAppIdsByMemberId(actor.getId(), PageRequest.of(0, count));
        return toGameListResponse(ownedGames);
    }

    // 파티 참가 취소
    // AOP에 필요한 파라미터
    @PartyPendingOnly
    @Transactional
    public void cancelPendingParty(Member actor, long partyId) {
        PartyMember me = PartyMemberContext.getPartyMember();

        me.deleteWithUpdateTotal();
    }

    // 파티 초대 승인
    // AOP에 필요한 파라미터
    @PartyInviterOnly
    @Transactional
    public void approvePartyInvitation(Member actor, long partyId) {
        Party party = PartyContext.getParty();

        PartyValidation.checkPartyCanJoin(party);
        PartyValidation.checkPartyIsNotFull(party);

        PartyMember me = PartyMemberContext.getPartyMember();

        me.promoteRole(PartyRole.MEMBER);
    }

    // 파티 초대 거절
    // AOP에 필요한 파라미터
    @PartyInviterOnly
    @Transactional
    public void rejectPartyInvitation(Member actor, long partyId) {
        PartyMember me = PartyMemberContext.getPartyMember();

        me.deleteWithUpdateTotal();
    }

    // 내 참여중인 파티 조회
    @Transactional(readOnly = true)
    public GetPartyMainResponse getMyParties(Member actor) {
        return this.getRecentActiveParty(actor);
    }

    // 내가 참여했던 파티들을 최근에 끝난 순으로 조회
    @Transactional(readOnly = true)
    public Page<GetPartyResponse> getPartiesLoggedByMe(Member actor, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Party> recentCompletedParties = this.partyRepository.findMembersRecentCompletedParties(
                actor.getId(), PartyStatus.COMPLETED, pageable);

        if (recentCompletedParties.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return this.getCompletedPartiesByMembers(recentCompletedParties, pageable);
    }

    // 타 유저 참여중인 파티 조회
    @Transactional(readOnly = true)
    public GetPartyMainResponse getMembersParties(long memberId) {
        Member actor = this.getActor(memberId);

        return this.getRecentActiveParty(actor);
    }

    // 타 유저가 참여햇던 파티들을 최근에 끝난 순으로 조회
    @Transactional(readOnly = true)
    public Page<GetPartyResponse> getPartiesLoggedByMember(long memberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Member actor = this.getActor(memberId);

        Page<Party> recentCompletedParties = this.partyRepository.findMembersRecentCompletedParties(
                actor.getId(), PartyStatus.COMPLETED, pageable);

        if (recentCompletedParties.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return getCompletedPartiesByMembers(recentCompletedParties, pageable);
    }

    private List<GameListResponse> toGameListResponse(List<Long> appIds) {
        return gameRepository.findAllByAppidIn(appIds).stream()
                .map(game -> GameListResponse.builder()
                        .appid(game.getAppid())
                        .name(game.getName())
                        .headerImage(game.getHeaderImage())
                        .genres(
                                game.getGenres().stream()
                                        .map(SteamGenre::getName)
                                        .toList()
                        )
                        .build())
                .toList();
    }

    // memberId로 Member 조회
    private Member getActor(long memberId) {
        return this.memberRepository.findById(memberId)
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwServiceException);
    }

    // 최근 파티 조회
    private GetPartyMainResponse getRecentActiveParty(Member actor) {
        List<PartyRole> partyRoles = List.of(PartyRole.OWNER, PartyRole.MEMBER);

        List<Party> myParties = this.partyRepository.findMembersActiveParties(actor.getId(), partyRoles,
                PartyStatus.COMPLETED);

        List<Long> myPartyIds = myParties.stream().map(Party::getId).toList();

        return new GetPartyMainResponse(
                PartyMergeUtils.mergePartyWithJoinData(
                        myParties,
                        this.partyRepository.findPartyTagsByPartyIds(myPartyIds),
                        this.partyRepository.findPartyMembersByPartyIds(myPartyIds)
                ));
    }

    // 유저가 참여했던 파티들을 최근에 끝난 순으로 조회
    private PageImpl<GetPartyResponse> getCompletedPartiesByMembers(Page<Party> completedParties, Pageable pageable) {
        List<Long> completedPartyIds = completedParties.stream()
                .map(Party::getId)
                .toList();

        List<GetPartyResponse> mergedList = PartyMergeUtils.mergePartyWithJoinData(
                completedParties.getContent(),
                this.partyRepository.findPartyTagsByPartyIds(completedPartyIds),
                this.partyRepository.findPartyMembersByPartyIds(completedPartyIds)
        );

        return new PageImpl<>(
                mergedList,
                pageable,
                completedParties.getTotalElements());
    }
}
