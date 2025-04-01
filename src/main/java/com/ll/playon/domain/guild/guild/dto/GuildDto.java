package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.entity.Guild;

import java.time.LocalDateTime;

public record GuildDto(
        Long id,
        String name,
        String description,
        int maxMembers,
        boolean isPublic,
        String guildImg,
        LocalDateTime createdAt
) {
    public static GuildDto from(Guild guild) {
        return new GuildDto(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getMaxMembers(),
                guild.isPublic(),
                guild.getGuildImg(),
                guild.getCreatedAt()
        );
    }
}
