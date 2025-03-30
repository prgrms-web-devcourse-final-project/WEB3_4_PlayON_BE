package com.ll.playon.global.initData;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.enums.*;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberRepository memberRepository;
    private final MemberSteamDataRepository memberSteamDataRepository;
    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final MemberService memberService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleUsers();
            self.makeSampleGuild();
        };
    }

    @Transactional
    public void makeSampleUsers() {
        if(memberRepository.count() != 0) return;

        Member sampleMember1 = Member.builder()
                .steamId(123L).username("sampleUser1").lastLoginAt(LocalDateTime.now()).build();
        memberRepository.save(sampleMember1);

        List<Long> gameAppIds = Arrays.asList(2246340L, 2680010L, 2456740L);
        memberService.saveUserGameList(gameAppIds, sampleMember1);

        Member sampleMember2 = Member.builder()
                .steamId(456L).username("sampleUser2").lastLoginAt(LocalDateTime.now()).build();
        memberRepository.save(sampleMember2);

        memberService.saveUserGameList(gameAppIds, sampleMember2);

        Member sampleMember3 = Member.builder()
                .steamId(789L).username("sampleUser3").lastLoginAt(LocalDateTime.now()).build();
        memberRepository.save(sampleMember3);

        memberService.saveUserGameList(gameAppIds, sampleMember3);
    }

    @Transactional
    public void makeSampleGuild() {
        if(guildRepository.count() != 0) return;

        Member owner = memberRepository.findById(1L).get();
        Member member1 = memberRepository.findById(2L).get();
        Member member2 = memberRepository.findById(3L).get();

        Guild guild = Guild.builder()
                .owner(owner)
                .name("테스트 길드")
                .description("샘플 데이터용 길드입니다.")
                .maxMembers(10)
                .game(789L)
                .partyStyle(PartyStyle.CASUAL)
                .gameSkill(GameSkill.HACKER)
                .genderFilter(GenderFilter.ALL)
                .activeTime(ActiveTime.NIGHT)
                .build();

        guildRepository.save(guild);

        GuildMember guildOwner = GuildMember.builder()
                .guild(guild)
                .member(owner)
                .guildRole(GuildRole.LEADER)
                .build();

        GuildMember guildMember1 = GuildMember.builder()
                .guild(guild)
                .member(member1)
                .guildRole(GuildRole.MANAGER)
                .build();

        GuildMember guildMember2 = GuildMember.builder()
                .guild(guild)
                .member(member2)
                .guildRole(GuildRole.MEMBER)
                .build();

        guildMemberRepository.save(guildOwner);
        guildMemberRepository.save(guildMember1);
        guildMemberRepository.save(guildMember2);
    }
}