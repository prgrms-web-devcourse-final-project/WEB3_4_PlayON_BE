package com.ll.playon.global.initData;

import com.ll.playon.domain.member.MemberRepository;
import com.ll.playon.domain.member.MemberSteamDataRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberRepository memberRepository;
    private final MemberSteamDataRepository memberSteamDataRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleUsers();
        };
    }

    @Transactional
    public void makeSampleUsers() {
        if(memberRepository.count() != 0) return;

        Member sampleMember1 = Member.builder()
                .steamId(123L).username("sampleUser1").lastLoginAt(LocalDateTime.now()).build();
        memberRepository.save(sampleMember1);

        List<Long> gameAppIds = Arrays.asList(2246340L, 2680010L, 2456740L);
        List<MemberSteamData> games = new ArrayList<>();
        for (Long appId : gameAppIds) {
            MemberSteamData game = MemberSteamData.builder()
                    .appId(appId).member(sampleMember1).build();
            games.add(game);
        }
        sampleMember1.getGames().addAll(games);

        memberSteamDataRepository.saveAll(games);

        Member sampleMember2 = Member.builder()
                .steamId(456L).username("sampleUser2").lastLoginAt(LocalDateTime.now()).build();
        memberRepository.save(sampleMember2);

        sampleMember2.getGames().addAll(games);
        memberSteamDataRepository.saveAll(games);

        Member sampleMember3 = Member.builder()
                .steamId(789L).username("sampleUser3").lastLoginAt(LocalDateTime.now()).build();
        memberRepository.save(sampleMember3);

        sampleMember3.getGames().addAll(games);
        memberSteamDataRepository.saveAll(games);
    }
}