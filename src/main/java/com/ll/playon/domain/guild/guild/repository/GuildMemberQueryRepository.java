package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuildMemberQueryRepository {
    Page<GuildMember> findByGuildOrderByRoleAndCreatedAt(Guild guild, Pageable pageable);
}
