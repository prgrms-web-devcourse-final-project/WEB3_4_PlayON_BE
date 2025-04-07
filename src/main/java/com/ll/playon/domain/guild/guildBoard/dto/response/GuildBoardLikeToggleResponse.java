package com.ll.playon.domain.guild.guildBoard.dto.response;

public record GuildBoardLikeToggleResponse(
        boolean liked,
        int likeCount
) {
    public static GuildBoardLikeToggleResponse of(boolean liked, int likeCount) {
        return new GuildBoardLikeToggleResponse(liked, likeCount);
    }
}
