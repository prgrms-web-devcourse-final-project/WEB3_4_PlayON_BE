package com.ll.playon.domain.guild.guildBoard.dto.response;

import java.net.URL;

public record GuildBoardCreateResponse(Long id, URL presignedUrl) {
    public static GuildBoardCreateResponse from(Long id, URL presignedUrl) {
        return new GuildBoardCreateResponse(id, presignedUrl);
    }
}