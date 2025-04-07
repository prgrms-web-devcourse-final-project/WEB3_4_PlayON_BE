package com.ll.playon.domain.guild.guildBoard.dto;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;

import java.time.LocalDateTime;

public record GuildBoardCommentDto(
        Long id,
        String authorNickname,
        String content,
        LocalDateTime createdAt
) {
    public static GuildBoardCommentDto from(GuildBoardComment comment) {
        return new GuildBoardCommentDto(
                comment.getId(),
                comment.getAuthor().getMember().getNickname(),
                comment.getComment(),
                comment.getCreatedAt()
        );
    }
}

