package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.title.entity.MemberTitle;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record getGuildMemberResponse(
        Long memberId,
        String username,
        String profileImg,
        String title,
        String  role,
        LocalDateTime joinedAt
) {
    public static getGuildMemberResponse from(GuildMember member) {
        return getGuildMemberResponse.builder()
                .memberId(member.getMember().getId())
                .username(member.getMember().getUsername())
                .profileImg(member.getMember().getProfileImg())
                .title(member.getMember().getMemberTitles().stream()
                        .filter(MemberTitle::isRepresentative)
                        .findFirst()
                        .map(mt -> mt.getTitle().getName())
                        .orElse(null))
                .role(member.getGuildRole().name())
                .joinedAt(member.getCreatedAt())
                .build();
    }
}
