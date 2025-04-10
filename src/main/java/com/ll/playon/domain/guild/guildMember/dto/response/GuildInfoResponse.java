package com.ll.playon.domain.guild.guildMember.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;

import java.time.LocalDate;
import java.util.List;

public record GuildInfoResponse(
        String name,
        String imageUrl,
        List<String> tags,
        LocalDate createdDate,
        String leaderUsername,
        int totalMemberCount,
        List<String> managerUsernames
) {
    public static GuildInfoResponse from(Guild guild, List<GuildMember> members, int totalCount) {
        GuildMember leaderMember = members.stream()
                .filter(m -> m.getGuildRole() == GuildRole.LEADER)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("길드장이 존재하지 않습니다."));

        String leaderUsername = leaderMember.getMember().getUsername();
        String imageUrl = leaderMember.getMember().getProfileImg();

        List<String> managers = members.stream()
                .filter(m -> m.getGuildRole() == GuildRole.MANAGER)
                .map(m -> m.getMember().getUsername())
                .toList();

        List<String> tagStrings = guild.getGuildTags().stream()
                .map(tag -> tag.getValue().getKoreanValue())
                .toList();

        return new GuildInfoResponse(
                guild.getName(),
                imageUrl,
                tagStrings,
                guild.getCreatedAt().toLocalDate(),
                leaderUsername,
                totalCount,
                managers
        );
    }
}