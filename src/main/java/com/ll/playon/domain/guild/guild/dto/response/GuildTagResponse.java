package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.GuildTag;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record GuildTagResponse(
        @NotBlank
        String type,

        @NotBlank
        String value
) {
    public GuildTagResponse(GuildTag guildTag) {
        this(guildTag.getType().getValue(), guildTag.getValue().getKoreanValue());
    }

    public static List<GuildTagResponse> fromList(List<GuildTag> guildTags) {
        return guildTags.stream()
                .map(GuildTagResponse::new)
                .toList();
    }
}
