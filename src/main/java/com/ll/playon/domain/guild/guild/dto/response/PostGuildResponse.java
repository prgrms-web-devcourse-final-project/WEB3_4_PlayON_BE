package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public record PostGuildResponse(
        Long id,
        String name,
        String description,
        int maxMembers,
        boolean isPublic,
        String guildImg,
        LocalDateTime createdAt,
        List<String> tags,
        URL presignedUrl
) {
    public static PostGuildResponse from(Guild guild, URL presignedUrl) {
        return new PostGuildResponse(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getMaxMembers(),
                guild.isPublic(),
                guild.getGuildImg(),
                guild.getCreatedAt(),
                guild.getGuildTags().stream()
                        .map(tag -> tag.getValue().getKoreanValue())
                        .toList(),
                presignedUrl
        );
    }
}
