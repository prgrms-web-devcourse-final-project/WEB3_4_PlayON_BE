package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;

import java.util.List;

public interface GuildMemberQueryRepository {
    List<GuildMember> findTopByGuildOrderByRoleAndCreatedAt(Guild guild, int limit);
}
