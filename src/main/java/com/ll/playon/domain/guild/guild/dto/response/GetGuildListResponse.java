package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;

import java.util.List;

public record GetGuildListResponse(
        long guildId,
        String guildImg,
        String name,
        String gameName,
        String description,
        int memberCount,
        List<GuildTagResponse> tags
) {
    public static GetGuildListResponse from(Guild guild) {
        return new GetGuildListResponse(
                guild.getId(),
                guild.getGuildImg(),
                guild.getGame() != null ? guild.getGame().getName() : null,
                guild.getGame().getName(),
                guild.getDescription(),
                guild.getMembers().size(),
                GuildTagResponse.fromList(guild.getGuildTags())
        );
    }
}