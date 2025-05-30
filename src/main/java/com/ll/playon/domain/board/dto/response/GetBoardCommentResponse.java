package com.ll.playon.domain.board.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetBoardCommentResponse(
        Long commentId,
        String nickname,
        String profileImg,
        boolean isAuthor,
        String comment,
        LocalDateTime createAt
) {
}
