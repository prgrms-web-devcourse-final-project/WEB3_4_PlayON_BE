package com.ll.playon.domain.guild.guildBoard.dto;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;
import com.ll.playon.domain.member.entity.Member;

import java.time.LocalDateTime;

public record GuildBoardCommentDto(
        Long id,
        String authorNickname,
        String authorProfileImg,
        String content,
        LocalDateTime createdAt,
        boolean isAuthor
) {
    public static GuildBoardCommentDto from(GuildBoardComment comment, Member actor) {
        return new GuildBoardCommentDto(
                comment.getId(),
                comment.getAuthor().getMember().getNickname(),
                comment.getAuthor().getMember().getProfileImg(),
                comment.getComment(),
                comment.getCreatedAt(),
                comment.getAuthor().getMember().getId().equals(actor.getId())
        );
    }
}

