package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;

import java.time.LocalDateTime;
import java.util.List;

public record GetGuildDetailResponse(
        Long id,
        String name,
        String description,
        int memberCount,
        int maxMembers,
        boolean isPublic,
        String guildImg,
        LocalDateTime createdAt,
        List<GuildTagResponse> tags,
        GuildRole myRole
) {
    public static GetGuildDetailResponse from(Guild guild, GuildRole myRole) {
        return new GetGuildDetailResponse(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getMembers().size(),
                guild.getMaxMembers(),
                guild.isPublic(),
                guild.getGuildImg(),
                guild.getCreatedAt(),
                GuildTagResponse.fromList(guild.getGuildTags()),
                myRole
        );
    }
}
