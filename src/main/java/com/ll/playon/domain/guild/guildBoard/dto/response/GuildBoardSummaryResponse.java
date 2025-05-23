package com.ll.playon.domain.guild.guildBoard.dto.response;

import com.ll.playon.domain.guild.guildBoard.dto.GuildSimpleDto;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.member.entity.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GuildBoardSummaryResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorNickname,
        String authorProfileImg,
        BoardTag tag,
        int likeCount,
        int commentCount,
        int hit,
        String imageUrl,
        LocalDateTime createdAt,
        GuildSimpleDto guild,
        boolean isAuthor
) {
    public static GuildBoardSummaryResponse from(GuildBoard board, int commentCount, Member actor) {
        Member author = board.getAuthor().getMember();
        return GuildBoardSummaryResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .authorId(author.getId())
                .authorNickname(author.getNickname())
                .authorProfileImg(author.getProfileImg())
                .tag(board.getTag())
                .likeCount(board.getLikeCount())
                .commentCount(commentCount)
                .hit(board.getHit())
                .imageUrl(board.getImageUrl())
                .createdAt(board.getCreatedAt())
                .guild(GuildSimpleDto.from(board.getGuild()))
                .isAuthor(author.getId().equals(actor.getId()))
                .build();
    }
}