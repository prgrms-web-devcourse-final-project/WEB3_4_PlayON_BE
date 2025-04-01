package com.ll.playon.domain.member.service;

import com.ll.playon.domain.member.dto.SignupMemberDetailResponse;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.steamAPI.SteamAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public Optional<Member> findById(Long id){
        return memberRepository.findById(id);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
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
        parsedMember.changeMemberId((long) payload.get("id"));

        return parsedMember;
    }

    public void steamLogin(Long steamId) {
        Member member = memberRepository.findBySteamId(steamId)
                .orElseThrow(ErrorCode.USER_NOT_REGISTERED::throwServiceException);

        handleSuccessfulLogin(member);
    }

    public SignupMemberDetailResponse steamSignup(Long steamId) {
        if (memberRepository.findBySteamId(steamId).isPresent()) {
            throw ErrorCode.USER_ALREADY_REGISTERED.throwServiceException();
        }

        Member member = signup(steamId);

        handleSuccessfulLogin(member);

        return new SignupMemberDetailResponse(
                member.getNickname(), member.getProfileImg(), member.getPlayStyle(),
                member.getSkillLevel(), member.getGender());
    }

    private void handleSuccessfulLogin(Member member) {
        member.setLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);

        // 쿠키 세팅
        userContext.makeAuthCookies(member);

        // 시큐리티 로그인
        userContext.setLogin(member);
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
}
