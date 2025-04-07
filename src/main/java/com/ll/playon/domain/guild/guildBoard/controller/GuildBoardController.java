package com.ll.playon.domain.guild.guildBoard.controller;

import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardUpdateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.response.GuildBoardCreateResponse;
import com.ll.playon.domain.guild.guildBoard.dto.response.GuildBoardSummaryResponse;
import com.ll.playon.domain.guild.guildBoard.enums.BoardSortType;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildBoard.service.GuildBoardService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
            @RequestParam(defaultValue = "LATEST") BoardSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (pageSize < 1 || pageSize > 100) {
            throw ErrorCode.PAGE_SIZE_LIMIT_EXCEEDED.throwServiceException();
        }

        Pageable pageable = PageRequest.of(page, pageSize, sort.getSort());
        Page<GuildBoardSummaryResponse> result = guildBoardService.getBoardList(guildId, tag, pageable);

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
}
