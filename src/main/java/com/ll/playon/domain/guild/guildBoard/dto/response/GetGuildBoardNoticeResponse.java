package com.ll.playon.domain.guild.guildBoard.dto.response;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import lombok.Builder;

@Builder
public record GetGuildBoardNoticeResponse(
        Long id,
        String title,
        String content,
        String authorNickname,
        String authorAvatar,
        int likeCount,
        int commentCount,
        String imageUrl
) {
    public static GetGuildBoardNoticeResponse from(GuildBoard board, int commentCount) {
        return GetGuildBoardNoticeResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .authorNickname(board.getAuthor().getMember().getNickname())
                .authorAvatar(board.getAuthor().getMember().getProfileImg())
                .likeCount(board.getLikeCount())
                .commentCount(commentCount)
                .imageUrl(board.getImageUrl())
                .build();
    }
}
