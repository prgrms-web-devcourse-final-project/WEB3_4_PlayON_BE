package com.ll.playon.domain.board.dto.response;

import lombok.Builder;

import java.net.URL;

@Builder
public record PutBoardResponse(
        Long boardId,
        URL presignedUrl
) {
}
