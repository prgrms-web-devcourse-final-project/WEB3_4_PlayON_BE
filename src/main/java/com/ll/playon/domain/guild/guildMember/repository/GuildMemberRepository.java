package com.ll.playon.domain.guild.guildMember.repository;

import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildMemberRepository extends JpaRepository<GuildMember, Long> {

}
