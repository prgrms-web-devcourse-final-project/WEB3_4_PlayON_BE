package com.ll.playon.domain.guild.guildBoard.dto.response;

import com.ll.playon.domain.guild.guildBoard.dto.GuildBoardCommentDto;
import com.ll.playon.domain.guild.guildBoard.dto.GuildSimpleDto;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.member.entity.Member;

import java.time.LocalDateTime;
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
        Long authorId,
        String authorProfileImg,
        List<GuildBoardCommentDto> comments,
        GuildSimpleDto guild,
        boolean isAuthor,
        LocalDateTime createdAt
) {
    public static GuildBoardDetailResponse from(GuildBoard board, List<GuildBoardCommentDto> comments, Member actor) {
        Member author = board.getAuthor().getMember();
        return new GuildBoardDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getTag().getDisplayName(),
                board.getHit(),
                board.getLikeCount(),
                board.getImageUrl(),
                author.getNickname(),
                author.getId(),
                author.getProfileImg(),
                comments,
                GuildSimpleDto.from(board.getGuild()),
                author.getId().equals(actor.getId()),
                board.getCreatedAt()
        );
    }
}
