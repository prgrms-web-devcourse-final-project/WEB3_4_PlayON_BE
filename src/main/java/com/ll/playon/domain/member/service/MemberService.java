package com.ll.playon.domain.member.service;

import com.ll.playon.domain.member.dto.MemberDetailDto;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.steamAPI.SteamAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final UserContext userContext;
    private final SteamAPI steamAPI;
    private final MemberSteamDataRepository memberSteamDataRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> findById(Long id){
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

        if (ObjectUtils.isEmpty(payload)) return null;

        Member parsedMember = Member.builder()
                .username((String) payload.get("username"))
                .role((Role) payload.get("role"))
                .build();
        parsedMember.changeMemberId(((Number)payload.get("id")).longValue());

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

        return new MemberDetailDto(
                member.getNickname(), member.getProfileImg(), member.getPlayStyle(),
                member.getSkillLevel(), member.getGender());
    }

    public MemberDetailDto signupNoSteam(String username, String password) {
        Optional<Member> memberOptional = memberRepository.findByUsername(username);
        if(memberOptional.isPresent()) {
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

        saveUserGameList(steamAPI.getUserGames(steamId), newMember);

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

                saveUserGameList(steamAPI.getUserGames(steamId), targetMember);
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
}
