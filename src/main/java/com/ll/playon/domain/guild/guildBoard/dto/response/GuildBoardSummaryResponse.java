package com.ll.playon.domain.guild.guildBoard.dto.response;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GuildBoardSummaryResponse(
        Long id,
        String title,
        String content,
        String authorNickname,
        BoardTag tag,
        int likeCount,
        int commentCount,
        int hit,
        String imageUrl,
        LocalDateTime createdAt
) {
    public static GuildBoardSummaryResponse from(GuildBoard board, int commentCount) {
        return GuildBoardSummaryResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .authorNickname(board.getAuthor().getMember().getNickname())
                .tag(board.getTag())
                .likeCount(board.getLikeCount())
                .commentCount(commentCount)
                .hit(board.getHit())
                .imageUrl(board.getImageUrl())
                .createdAt(board.getCreatedAt())
                .build();
    }
}
