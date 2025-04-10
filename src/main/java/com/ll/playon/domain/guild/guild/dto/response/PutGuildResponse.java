package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public record PutGuildResponse(
        Long id,
        String name,
        String description,
        int maxMembers,
        boolean isPublic,
        LocalDateTime createdAt,
        List<GuildTagResponse> tags,
        URL presignedUrl
) {
    public static PutGuildResponse from(Guild guild, URL presignedUrl) {
        return new PutGuildResponse(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getMaxMembers(),
                guild.isPublic(),
                guild.getCreatedAt(),
                GuildTagResponse.fromList(guild.getGuildTags()),
                presignedUrl
        );
    }
}
