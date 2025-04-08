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
        List<String> tags
) {
    public static GetGuildListResponse from(Guild guild) {
        return new GetGuildListResponse(
                guild.getId(),
                guild.getGuildImg(),
                guild.getName(),
                guild.getGame().getName(),
                guild.getDescription(),
                guild.getMembers().size(),
                guild.getGuildTags().stream()
                        .map(tag -> tag.getValue().getKoreanValue())
                        .toList()
        );
    }
}