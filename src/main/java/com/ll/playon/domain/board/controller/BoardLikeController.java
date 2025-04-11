package com.ll.playon.domain.board.controller;

import com.ll.playon.domain.board.service.BoardLikeService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BoardLike", description = "자유게시판 좋아요 기능")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardLikeController {
    private final UserContext userContext;
    private final BoardLikeService boardLikeService;

    @PostMapping("/{boardId}/like")
    @Operation(summary = "게시글 좋아요 등록")
    public RsData<String> like(@PathVariable Long boardId) {
        boardLikeService.like(boardId, userContext.getActor());
        return RsData.success(HttpStatus.OK, "좋아요 등록 성공");
    }

    @DeleteMapping("/{boardId}/like")
    @Operation(summary = "게시글 좋아요 취소")
    public RsData<String> cancelLike(@PathVariable Long boardId) {
        boardLikeService.cancelLike(boardId, userContext.getActor());
        return RsData.success(HttpStatus.OK, "좋아요 취소 성공");
    }
}
