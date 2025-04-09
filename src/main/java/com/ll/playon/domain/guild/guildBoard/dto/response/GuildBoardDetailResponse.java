package com.ll.playon.domain.guild.guildBoard.dto.response;

import com.ll.playon.domain.guild.guildBoard.dto.GuildBoardCommentDto;
import com.ll.playon.domain.guild.guildBoard.dto.GuildSimpleDto;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;

import java.util.List;

public record GuildBoardDetailResponse(
        Long id,
        String title,
        String content,
        String tag,
        int hit,
        int likeCount,
        String imageUrl,
        String authorNickname,
        List<GuildBoardCommentDto> comments,
        GuildSimpleDto guild
) {
    public static GuildBoardDetailResponse from(GuildBoard board, List<GuildBoardCommentDto> comments) {
        return new GuildBoardDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getTag().getDisplayName(),
                board.getHit(),
                board.getLikeCount(),
                board.getImageUrl(),
                board.getAuthor().getMember().getNickname(),
                comments,
                GuildSimpleDto.from(board.getGuild())
        );
    }
}

