package com.ll.playon.domain.board.controller;

import com.ll.playon.domain.board.dto.request.PostBoardRequest;
import com.ll.playon.domain.board.dto.request.PutBoardRequest;
import com.ll.playon.domain.board.dto.response.GetBoardDetailResponse;
import com.ll.playon.domain.board.dto.response.GetBoardListResponse;
import com.ll.playon.domain.board.dto.response.PostBoardResponse;
import com.ll.playon.domain.board.dto.response.PutBoardResponse;
import com.ll.playon.domain.board.enums.BoardCategory;
import com.ll.playon.domain.board.enums.BoardSortType;
import com.ll.playon.domain.board.service.BoardService;
import com.ll.playon.domain.guild.guild.dto.request.PostImageUrlRequest;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Board", description = "자유게시판 기능")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserContext userContext;

    @PostMapping
    @Operation(summary = "게시글 작성")
    public RsData<PostBoardResponse> createBoard(@RequestBody @Valid PostBoardRequest request) {
        return RsData.success(HttpStatus.CREATED, boardService.createBoard(request, userContext.getActor()));
    }

    @PostMapping("/{boardId}/img")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "게시글 이미지 URL 저장")
    public void saveImageUrl(@PathVariable long boardId, @RequestBody PostImageUrlRequest newImgUrl) {
        boardService.saveImageUrl(boardId, newImgUrl);
    }

    @PutMapping("/{boardId}")
    @Operation(summary = "게시글 수정")
    public RsData<PutBoardResponse> updateBoard(@PathVariable long boardId, @RequestBody @Valid PutBoardRequest request) {
        return RsData.success(HttpStatus.OK, boardService.modifyBoard(boardId, request, userContext.getActor()));
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제")
    public RsData<String> deleteBoard(@PathVariable long boardId) {
        boardService.deleteBoard(boardId, userContext.getActor());
        return RsData.success(HttpStatus.OK, "ok");
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 상세조회")
    public RsData<GetBoardDetailResponse> getBoard(@PathVariable long boardId) {
        return RsData.success(HttpStatus.OK, boardService.getBoardDetail(boardId));
    }

    @GetMapping("/list")
    @Operation(summary = "게시글 목록")
    public RsData<PageDto<GetBoardListResponse>> getBoards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "latest") BoardSortType sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BoardCategory category
    ) {
        return RsData.success(HttpStatus.OK, boardService.getBoardList(page, pageSize, sort, keyword, category));
    }




}
