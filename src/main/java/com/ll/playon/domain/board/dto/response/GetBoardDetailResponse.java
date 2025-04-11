package com.ll.playon.domain.board.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetBoardDetailResponse(
        Long boardId,
        String authorNickname,
        String profileImg,
        String title,
        String boardTitle,
        LocalDateTime createAt,
        String imgUrl,
        String content,
        long hit,
        long like,
        String boardCategory
) {
}
