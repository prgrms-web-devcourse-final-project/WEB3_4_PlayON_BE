package com.ll.playon.domain.board.dto.response;

import lombok.Builder;

@Builder
public record PutBoardCommentResponse(
        Long commentId
) {
}
