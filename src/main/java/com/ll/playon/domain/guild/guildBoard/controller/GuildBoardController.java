package com.ll.playon.domain.guild.guildBoard.controller;

import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCommentCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCommentUpdateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardUpdateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.response.*;
import com.ll.playon.domain.guild.guildBoard.enums.BoardSortType;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildBoard.service.GuildBoardService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guilds")
@RequiredArgsConstructor
public class GuildBoardController {

    private final GuildBoardService guildBoardService;
    private final UserContext userContext;

    @GetMapping("/{guildId}/board")
    public RsData<Page<GuildBoardSummaryResponse>> getBoards(
            @PathVariable Long guildId,
            @RequestParam(required = false) BoardTag tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "LATEST") BoardSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        if (pageSize < 1 || pageSize > 100) {
            throw ErrorCode.PAGE_SIZE_LIMIT_EXCEEDED.throwServiceException();
        }

        Pageable pageable = PageRequest.of(page, pageSize, sort.getSort());
        Page<GuildBoardSummaryResponse> result = guildBoardService.getBoardList(guildId, tag, keyword, pageable);

        return RsData.success(HttpStatus.OK, result);
    }

    @PostMapping("/{guildId}/board")
    public RsData<GuildBoardCreateResponse> createBoard(
            @PathVariable Long guildId,
            @RequestBody @Valid GuildBoardCreateRequest request
    ) {
        Member actor = userContext.getActor();
        GuildBoardCreateResponse response = guildBoardService.createBoard(guildId, request, actor);
        return RsData.success(HttpStatus.CREATED, response);
    }

    @PutMapping("/{guildId}/board/{boardId}")
    public RsData<String> updateBoard(
            @PathVariable Long guildId,
            @PathVariable Long boardId,
            @RequestBody @Valid GuildBoardUpdateRequest request
    ){
        Member actor = userContext.getActor();
        guildBoardService.updateBoard(guildId, boardId, request, actor);
        return RsData.success(HttpStatus.OK, "수정되었습니다.");
    }

    @DeleteMapping("/{guildId}/board/{boardId}")
    public RsData<String> deleteBoard(
            @PathVariable Long guildId,
            @PathVariable Long boardId
    ) {
        Member actor = userContext.getActor();
        guildBoardService.deleteBoard(guildId, boardId, actor);
        return RsData.success(HttpStatus.OK, "삭제되었습니다.");
    }

    @GetMapping("/{guildId}/board/{boardId}")
    public RsData<GuildBoardDetailResponse> getBoardDetail(
            @PathVariable Long guildId,
            @PathVariable Long boardId
    ){
        Member actor = userContext.getActor();
        GuildBoardDetailResponse response=guildBoardService.getBoardDetail(guildId, boardId,actor);
        return RsData.success(HttpStatus.OK, response);
    }

    @PostMapping("/{guildId}/board/{boardId}/like")
    public RsData<GuildBoardLikeToggleResponse> toggleLike(
            @PathVariable Long guildId,
            @PathVariable Long boardId
    ){
        Member actor= userContext.getActor();
        GuildBoardLikeToggleResponse response=guildBoardService.toggleLike(guildId, boardId, actor);
        return RsData.success(HttpStatus.OK, response);
    }

    @PostMapping("/{guildId}/board/{boardId}/comments")
    public RsData<GuildBoardCommentCreateResponse> createComment(
            @PathVariable Long guildId,
            @PathVariable Long boardId,
            @RequestBody @Valid GuildBoardCommentCreateRequest request
    ) {
        Member actor = userContext.getActor();
        GuildBoardCommentCreateResponse response = guildBoardService.createComment(guildId, boardId, request, actor);
        return RsData.success(HttpStatus.CREATED, response);
    }

    @PutMapping("/{guildId}/board/{boardId}/comments/{commentId}")
    public RsData<String> updateComment(
            @PathVariable Long guildId,
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @RequestBody @Valid GuildBoardCommentUpdateRequest request
    ){
        Member actor = userContext.getActor();
        guildBoardService.updateComment(guildId, boardId, commentId, request, actor);
        return RsData.success(HttpStatus.OK, "수정되었습니다.");
    }

    @DeleteMapping("/{guildId}/board/{boardId}/comments/{commentId}")
    public RsData<String> deleteComment(
            @PathVariable Long guildId,
            @PathVariable Long boardId,
            @PathVariable Long commentId
    ) {
        Member actor = userContext.getActor();
        guildBoardService.deleteComment(guildId, boardId, commentId, actor);
        return RsData.success(HttpStatus.OK, "삭제되었습니다.");
    }
}
