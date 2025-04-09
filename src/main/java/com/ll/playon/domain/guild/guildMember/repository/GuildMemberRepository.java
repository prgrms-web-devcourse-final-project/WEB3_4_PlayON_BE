package com.ll.playon.domain.guild.guildMember.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildMemberRepository extends JpaRepository<GuildMember, Long> {
    Optional<GuildMember> findByGuildAndMember(Guild guild, Member actor);
    long countByGuildId(Long guildId);
    List<GuildMember> findAllByGuild(Guild guild);
    boolean existsByGuildAndMember(Guild guild, Member member);
    List<GuildMember> findAllByMember(Member member);

    @Query("""
            SELECT m.nickname
            FROM GuildMember gm
            JOIN gm.member m
            WHERE gm.guild.id = :guildId AND gm.guildRole = 'MANAGER'
        """)
    List<String> findManagerNicknamesByGuildId(@Param("guildId") Long guildId);
}
