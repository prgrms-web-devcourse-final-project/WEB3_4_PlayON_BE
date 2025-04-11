package com.ll.playon.domain.board.controller;

import com.ll.playon.domain.board.dto.request.PostBoardCommentRequest;
import com.ll.playon.domain.board.dto.request.PutBoardCommentRequest;
import com.ll.playon.domain.board.dto.response.GetBoardCommentResponse;
import com.ll.playon.domain.board.dto.response.PostBoardCommentResponse;
import com.ll.playon.domain.board.dto.response.PutBoardCommentResponse;
import com.ll.playon.domain.board.service.BoardCommentService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BoardComment", description = "자유게시판 댓글 기능")
@RestController
@RequestMapping("/api/boards/{boardId}/comments")
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService boardCommentService;
    private final UserContext userContext;

    @PostMapping
    @Operation(summary = "댓글 작성")
    public RsData<PostBoardCommentResponse> createComment(
            @PathVariable Long boardId,
            @RequestBody @Valid PostBoardCommentRequest request
    ) {
        return RsData.success(HttpStatus.CREATED, boardCommentService.createComment(boardId, request, userContext.getActor()));
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정")
    public RsData<PutBoardCommentResponse> updateComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @RequestBody @Valid PutBoardCommentRequest request
    ) {
        return RsData.success(HttpStatus.OK, boardCommentService.modifyComment(boardId, commentId, request, userContext.getActor()));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제")
    public RsData<String> deleteComment(@PathVariable Long boardId,
                                  @PathVariable Long commentId) {
        boardCommentService.deleteComment(boardId, commentId, userContext.getActor());
        return RsData.success(HttpStatus.OK, "ok");
    }

    @GetMapping
    @Operation(summary = "댓글 목록 조회")
    public RsData<PageDto<GetBoardCommentResponse>> getComments(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return RsData.success(HttpStatus.OK, boardCommentService.getComments(boardId, page, pageSize));
    }
}
