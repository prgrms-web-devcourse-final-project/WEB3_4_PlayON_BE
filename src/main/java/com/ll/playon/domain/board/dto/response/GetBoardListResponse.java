package com.ll.playon.domain.board.dto.response;

import com.ll.playon.domain.board.enums.BoardCategory;
import lombok.Builder;

@Builder
public record GetBoardListResponse(
        Long boardId,
        String authorNickname,
        String profileImg,
        String title, // 대표 칭호
        String boardTitle,
        String boardContent, // 일부만
        long hit,
        long likeCount,
        long commentCount,
        BoardCategory boardCategory,
        String imageUrl
) {
    public String getBoardCategory() {
        return boardCategory.getValue(); // 한글 값 반환
    }
}
