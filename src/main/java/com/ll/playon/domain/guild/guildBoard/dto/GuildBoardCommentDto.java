package com.ll.playon.domain.guild.guildBoard.dto;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;
import com.ll.playon.domain.member.entity.Member;

import java.time.LocalDateTime;

public record GuildBoardCommentDto(
        Long id,
        Long authorId,
        String authorNickname,
        String authorProfileImg,
        String content,
        boolean isAuthor,
        LocalDateTime createdAt
) {
    public static GuildBoardCommentDto from(GuildBoardComment comment, Member actor) {
        Member author = comment.getAuthor().getMember();
        return new GuildBoardCommentDto(
                comment.getId(),
                author.getId(),
                author.getNickname(),
                author.getProfileImg(),
                comment.getComment(),
                author.getId().equals(actor.getId()),
                comment.getCreatedAt()
        );
    }
}
