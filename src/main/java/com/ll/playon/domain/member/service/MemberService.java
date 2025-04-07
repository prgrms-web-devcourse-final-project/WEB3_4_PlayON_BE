package com.ll.playon.domain.member.service;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.game.game.service.GameService;
import com.ll.playon.domain.game.scheduler.repository.WeeklyGameRepository;
import com.ll.playon.domain.member.dto.GetMembersResponse;
import com.ll.playon.domain.member.dto.MemberDetailDto;
import com.ll.playon.domain.member.dto.MemberProfileResponse;
import com.ll.playon.domain.member.dto.ProfileMemberDetailDto;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.steamAPI.SteamAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    private final TitleEvaluator titleEvaluator;
    private final WeeklyGameRepository weeklyGameRepository;

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
        memberRepository.save(newMember);

        List<Long> userGames = steamAPI.getUserGames(steamId);
        if(!userGames.isEmpty()) {
            SteamGenre preferredGenre = steamAPI.getPreferredGenre(userGames);
            memberRepository.save(newMember.toBuilder().preferredGenre(preferredGenre).build());
            saveUserGameList(userGames, newMember);
        }

        // 스팀 게임 소유 칭호
        titleEvaluator.check(ConditionType.STEAM_GAME_COUNT, newMember);

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

    public void saveUserGameList(List<Long> gameList, Member member) {
        List<MemberSteamData> games = gameList.stream()
                .map(appId -> MemberSteamData.builder()
                        .appId(appId)
                        .member(member)
                        .build())
                .toList();

        member.getGames().addAll(games);
        memberSteamDataRepository.saveAll(games);
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

                    List<Long> userGames = steamAPI.getUserGames(steamId);
                    if(!userGames.isEmpty()) {
                        SteamGenre preferredGenre = steamAPI.getPreferredGenre(userGames);
                        memberRepository.save(targetMember.toBuilder().preferredGenre(preferredGenre).build());
                        saveUserGameList(userGames, targetMember);
                    }

                    return targetMember;
                })
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);
    }

    public void modifyMember(MemberDetailDto req, Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자 정보 수정 및 저장 (방어적 복사 적용해봄)
        memberRepository.save(member.toBuilder()
                .nickname(req.nickname())
                .profileImg(req.profileImg())
                .playStyle(req.playStyle())
                .skillLevel(req.skillLevel())
                .gender(req.gender())
                .build());
    }

    public void deactivateMember(Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자가 소유한 게임 목록 삭제
        memberSteamDataRepository.deleteById(member.getId());

        // 연결된 길드, 파티, 파티로그 등 남기기 위해서 엔티티를 삭제하지는 않음
        memberRepository.save(member.toBuilder()
                .steamId(null)  // 스팀 연결 해제
                .username("DELETED_" + UUID.randomUUID())
                .nickname("탈퇴한 사용자")
                .isDeleted(true)
                .build());
    }

    public MemberProfileResponse me(Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자 정보 조회 후 Dto 에 담기
        ProfileMemberDetailDto profileMemberDetailDto = new ProfileMemberDetailDto(
                member.getSteamId(), member.getUsername(), member.getNickname(), member.getProfileImg(),
                member.getLastLoginAt(), member.getPlayStyle(), member.getSkillLevel(),
                member.getGender(), member.getPreferredGenre()
        );

        // 보유한 게임 목록 조회
        List<MemberSteamData> gamesList = memberSteamDataRepository.findAllByMemberId(actor.getId());

        // 모든 정보 MemberProfileResponse 에 담기
        return new MemberProfileResponse(profileMemberDetailDto, getMemberOwnedGamesDto(gamesList));
    }

    private List<GameListResponse> getMemberOwnedGamesDto(List<MemberSteamData> gamesList) {
        final List<SteamGame> gameList = gameRepository.findAllByAppidIn(gamesList.stream().map(MemberSteamData::getAppId).toList()); // DB 에 없는 게임은 제외됨
        return gameService.makeGameListWithoutGenre(gameList);
    }

    public List<GetMembersResponse> findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).stream()
                .map(member ->
                        new GetMembersResponse(member.getSteamId(), member.getUsername(), member.getProfileImg()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GameListResponse> getOwnedGamesByMember(int count, Member actor) {

        List<Long> ownedGames = memberSteamDataRepository.findAppIdsByMemberId(actor.getId(), PageRequest.of(0, count));

        if (ownedGames.isEmpty()) {
            // 보유 게임이 없으면 임의의 게임 필요
            return toGameListResponse(List.of(730L, 578080L, 359550L));
        }

        return toGameListResponse(ownedGames);
    }

    private List<GameListResponse> toGameListResponse(List<Long> appIds) {
        return gameRepository.findAllByAppidIn(appIds).stream()
                .map(game -> GameListResponse.builder()
                        .appid(game.getAppid())
                        .name(game.getName())
                        .headerImage(game.getHeaderImage())
                        .gameGenres(
                                game.getGenres().stream()
                                        .map(SteamGenre::getName)
                                        .toList()
                        )
                        .build())
                .toList();
    }
}
