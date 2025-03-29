package com.ll.playon.global.initData;

import com.ll.playon.domain.member.MemberRepository;
import com.ll.playon.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberRepository memberRepository;

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
        Member sampleMember = Member.builder()
                .steamId(123L).username("sampleUser").lastLoginAt(LocalDateTime.now()).build();
        memberRepository.save(sampleMember);
    }
}