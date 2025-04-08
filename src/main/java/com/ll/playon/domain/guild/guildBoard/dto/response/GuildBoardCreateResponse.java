package com.ll.playon.domain.guild.guildBoard.dto.response;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;

public record GuildBoardCreateResponse(Long id) {
    public static GuildBoardCreateResponse from(Long id) {
        return new GuildBoardCreateResponse(id);
    }
}
