package com.ll.playon.domain.guild.guildMember.dto.response;

import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.member.entity.Member;

import java.time.LocalDateTime;

public record GuildMemberResponse(
        Long memberId,
        String username,
        String profileImg,
        GuildRole guildRole,
        LocalDateTime joinedAt,
        LocalDateTime lastLoginAt,
        int postCount
) {
    public static GuildMemberResponse from(GuildMember guildMember, int postCount) {
        Member member = guildMember.getMember();
        return new GuildMemberResponse(
                member.getId(),
                member.getUsername(),
                member.getProfileImg(),
                guildMember.getGuildRole(),
                guildMember.getCreatedAt(),
                member.getLastLoginAt(),
                postCount
        );
    }
}