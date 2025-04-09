package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;

import java.util.List;

import java.util.List;

public interface GuildMemberRepositoryCustom {
    List<GuildMember> findTopNByGuildOrderByRoleAndCreatedAt(Guild guild, int limit);
}
