package com.ll.playon.domain.game.game.controller;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.dto.response.*;
import com.ll.playon.domain.game.game.service.GameService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.validation.GlobalValidation;
import com.ll.playon.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "GameController")
public class GameController {
    private final GameService gameService;
    private final UserContext userContext;

    @GetMapping("/ranking")
    @Operation(summary = "스팀 인기게임 TOP5")
    public RsData<List<GameListResponse>> getSteamGameTopFive() {
        return RsData.success(HttpStatus.OK, gameService.getGameRanking());
    }

    @GetMapping("/recommend")
    @Operation(summary = "사용자 선호장르 인기게임 추천")
    public RsData<List<GameListResponse>> getRecommendedGames() {
        Member actor = userContext.getActor();
        if(ObjectUtils.isEmpty(actor)) throw ErrorCode.UNAUTHORIZED.throwServiceException();
        return RsData.success(HttpStatus.OK, gameService.getGameRecommendations(actor));
    }

    @GetMapping("/{appid}/details")
    @Operation(summary = "게임 상세 정보 및 파티/로그 목록 조회")
    public RsData<GameDetailWithPartyResponse> getGameDetail(@PathVariable Long appid) {
        return RsData.success(HttpStatus.OK, gameService.getGameDetailWithParties(appid));
    }

    @GetMapping("/list")
    @Operation(summary = "필터 조건에 맞는 게임 목록 조회")
    public RsData<PageDto<GameSummaryResponse>> getFilteredGames(
            @ModelAttribute GameSearchCondition condition,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        GlobalValidation.checkPageSize(pageSize);

        return RsData.success(HttpStatus.OK, gameService.searchGames(condition, PageRequest.of(page - 1, pageSize)));
    }

    @GetMapping("/search")
    @Operation(summary = "게임 키워드 자동완성")
    public RsData<List<GameAutoCompleteResponse>> searchGameByKeyword(@RequestParam String keyword) {
        return RsData.success(HttpStatus.OK, gameService.autoCompleteGames(keyword));
    }

    @GetMapping("/{appid}/party")
    @Operation(summary = "게임 관련 파티 목록 조회")
    public RsData<PageDto<PartySummaryResponse>> getGameParties(
            @PathVariable Long appid,
            @PageableDefault(size = 12, sort = "partyAt") Pageable pageable
    ) {
        return RsData.success(HttpStatus.OK, gameService.getGameParties(appid, pageable));
    }

    @GetMapping("/{appid}/logs")
    @Operation(summary = "게임 관련 파티 로그 목록 조회")
    public RsData<PageDto<PartyLogSummaryResponse>> getGamePartyLogs(
            @PathVariable Long appid,
            @PageableDefault(size = 12, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return RsData.success(HttpStatus.OK, gameService.getGamePartyLogs(appid, pageable));
    }

    @GetMapping("/popular")
    @Operation(summary = "유저가 많이 선택한 게임")
    public RsData<List<GetWeeklyPopularGameResponse>> getWeeklyPopularGames() {
        return RsData.success(HttpStatus.OK, gameService.getWeeklyPopularGames(LocalDate.now().with(DayOfWeek.MONDAY)));
    }

    @GetMapping("/recommend/friends")
    @Operation(summary = "최근 함께 파티한 유저의 게임")
    public RsData<List<GetRecommendedGameResponse>> getFriendRecommendedGames() {
        Member actor = userContext.getActor();
        if(ObjectUtils.isEmpty(actor)) throw ErrorCode.UNAUTHORIZED.throwServiceException();
        return RsData.success(HttpStatus.OK, gameService.recommendGamesForMember(actor.getId()));
    }

    @GetMapping("/recommend/playtime/top")
    @Operation(summary = "플레이타임 긴 게임")
    public RsData<List<GetRecommendedGameResponse>> getTopPlaytimeGames() {
        return RsData.success(HttpStatus.OK, gameService.getTopPlaytimeGames(LocalDate.now().with(DayOfWeek.MONDAY)));
    }
}
