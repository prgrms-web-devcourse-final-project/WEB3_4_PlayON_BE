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
        int postCount // TODO: 길드 게시판 구현 후 멤버별 글 갯수 집계 예정
) {
    public static GuildMemberResponse from(GuildMember guildMember) {
        Member member = guildMember.getMember();
        return new GuildMemberResponse(
                member.getId(),
                member.getUsername(),
                member.getProfileImg(),
                guildMember.getGuildRole(),
                guildMember.getCreatedAt(),
                member.getLastLoginAt(),
                0 // TODO: 게시글 개수 조회 로직 필요
        );
    }
}

