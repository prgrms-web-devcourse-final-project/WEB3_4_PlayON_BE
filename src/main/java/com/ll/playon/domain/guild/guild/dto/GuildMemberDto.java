package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;

import java.time.LocalDateTime;

public record GuildMemberDto(
        Long memberId,
        String username,
        GuildRole role,
        LocalDateTime joinedAt
) {
    public static GuildMemberDto from(GuildMember gm) {
        return new GuildMemberDto(
                gm.getMember().getId(),
                gm.getMember().getUsername(),
                gm.getGuildRole(),
                gm.getCreatedAt()
        );
    }
}