package com.ll.playon.domain.guild.guildBoard.dto;

import com.ll.playon.domain.guild.guild.entity.Guild;

public record GuildSimpleDto(
        Long id,
        String name,
        String description,
        String guildImg,
        int memberCount
) {
    public static GuildSimpleDto from(Guild guild) {
        return new GuildSimpleDto(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getGuildImg(),
                guild.getMembers().size()
        );
    }
}
