package com.ll.playon.domain.guild.guildBoard.dto.response;

import java.net.URL;

public record GuildBoardUpdateResponse(Long id, URL presignedUrl) {
    public static GuildBoardUpdateResponse from(Long id, URL presignedUrl) {
        return new GuildBoardUpdateResponse(id, presignedUrl);
    }
}
