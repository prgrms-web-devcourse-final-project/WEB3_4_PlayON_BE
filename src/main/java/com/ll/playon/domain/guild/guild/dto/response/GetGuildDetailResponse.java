package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.enums.GuildMemberRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GetGuildDetailResponse(
        Long id,
        String name,
        String description,
        String leaderName,
        String leaderImg,
        int memberCount,
        int maxMembers,
        boolean isPublic,
        String guildImg,
        LocalDateTime createdAt,
        GuildMemberRole myRole,
        List<GuildTagResponse> tags
) {
    public static GetGuildDetailResponse from(Guild guild, GuildMemberRole myRole) {
        return GetGuildDetailResponse.builder()
                .id(guild.getId())
                .name(guild.getName())
                .description(guild.getDescription())
                .leaderName(guild.getOwner().getNickname())
                .leaderImg(guild.getOwner().getProfileImg())
                .memberCount(guild.getMembers().size())
                .maxMembers(guild.getMaxMembers())
                .isPublic(guild.isPublic())
                .guildImg(guild.getGuildImg())
                .createdAt(guild.getCreatedAt())
                .myRole(myRole)
                .tags(GuildTagResponse.fromList(guild.getGuildTags()))
                .build();
    }
}
