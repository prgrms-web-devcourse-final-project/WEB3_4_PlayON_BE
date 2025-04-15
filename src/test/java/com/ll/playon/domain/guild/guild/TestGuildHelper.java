package com.ll.playon.domain.guild.guild;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class TestGuildHelper {

    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final MemberRepository memberRepository;

    public TestGuildHelper(GuildRepository guildRepository, GuildMemberRepository guildMemberRepository, MemberRepository memberRepository) {
        this.guildRepository = guildRepository;
        this.guildMemberRepository = guildMemberRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * username 또는 ID로 멤버 조회
     */
    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("멤버 없음"));
    }

    public Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("멤버 없음"));
    }

    /**
     * 길드 생성
     */
    public Guild createGuild(String name, boolean isPublic, Member owner) {
        Guild guild = Guild.builder()
                .name(name)
                .description("테스트용")
                .isPublic(isPublic)
                .maxMembers(10)
                .owner(owner)
                .build();
        return guildRepository.save(guild);
    }

    /**
     * 길드 멤버 추가
     */
    public GuildMember addMemberToGuild(Guild guild, Member member, GuildRole role) {
        GuildMember guildMember = GuildMember.builder()
                .guild(guild)
                .member(member)
                .guildRole(role)
                .build();
        return guildMemberRepository.save(guildMember);
    }

    /**
     * 💡 자주 쓰는 패턴 ①
     * 특정 유저가 길드를 만들고 길드장이 되는 경우
     */
    public Guild createGuildWithLeader(String guildName, boolean isPublic, String username) {
        Member owner = findMember(username);
        Guild guild = createGuild(guildName, isPublic, owner);
        addMemberToGuild(guild, owner, GuildRole.LEADER);
        return guild;
    }

    /**
     * 💡 자주 쓰는 패턴 ②
     * 공개 길드 + 길드장 생성
     */
    public Guild createPublicGuildWithLeader(String guildName, String username) {
        return createGuildWithLeader(guildName, true, username);
    }

    /**
     * 💡 자주 쓰는 패턴 ③
     * 비공개 길드 + 길드장 생성
     */
    public Guild createPrivateGuildWithLeader(String guildName, String username) {
        return createGuildWithLeader(guildName, false, username);
    }
}

