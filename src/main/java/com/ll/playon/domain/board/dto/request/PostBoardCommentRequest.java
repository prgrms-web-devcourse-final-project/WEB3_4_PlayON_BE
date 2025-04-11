package com.ll.playon.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostBoardCommentRequest(
        @NotBlank String comment
) {
}
