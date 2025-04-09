package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;

import java.util.List;

public record GetRecommendGuildResponse(
        Long guildId,
        String name,
        String description,
        String guildImg,
        int memberCount,
        List<GuildTagResponse> tags
) {
    public static GetRecommendGuildResponse from(Guild guild) {
        return new GetRecommendGuildResponse(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getGuildImg(),
                guild.getMembers().size(),
                GuildTagResponse.fromList(guild.getGuildTags())
        );
    }
}
