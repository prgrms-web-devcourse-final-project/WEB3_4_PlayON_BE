package com.ll.playon.domain.member.service;

import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.steamAPI.SteamAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        if (payload == null) return null;

        long id = (long) payload.get("id");
        String username = (String) payload.get("username");
        Role role = (Role) payload.get("role");

        return new Member(id, username, role);
    }

    public void signupOrSignin(String steamUserId) {
        Long steamId = Long.valueOf(steamUserId);
        Optional<Member> optionalMember = memberRepository.findBySteamId(steamId);

        // 새로운 회원을 생성하거나 기존 회원 조회
        Member member;
        if(optionalMember.isEmpty()) {
            member = signup(steamId);
        } else {
            member = optionalMember.get();
        }
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
                .username(profile.get("username") + " #" + steamId)
                .profile_img(profile.get("profileImg"))
                .apiKey(UUID.randomUUID().toString())
                .role(Role.USER)
                .build();
        memberRepository.save(newMember);

        List<Long> gameList = steamAPI.getUserGames(steamId);

        saveUserGameList(gameList, newMember);

        return newMember;
    }

    public void saveUserGameList(List<Long> gameList, Member member) {
        List<MemberSteamData> games = new ArrayList<>();
        for (Long appId : gameList) {
            MemberSteamData game = MemberSteamData.builder()
                    .appId(appId).member(member).build();
            games.add(game);
        }
        member.getGames().addAll(games);

        memberSteamDataRepository.saveAll(games);
    }
}
