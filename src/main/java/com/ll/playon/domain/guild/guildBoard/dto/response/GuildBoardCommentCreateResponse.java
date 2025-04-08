package com.ll.playon.domain.guild.guildBoard.dto.response;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;

public record GuildBoardCommentCreateResponse(
        Long id
) {
    public static GuildBoardCommentCreateResponse from(GuildBoardComment comment) {
        return new GuildBoardCommentCreateResponse(comment.getId());
    }
}
