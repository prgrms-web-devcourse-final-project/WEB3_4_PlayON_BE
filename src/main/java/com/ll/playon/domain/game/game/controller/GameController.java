package com.ll.playon.domain.game.game.controller;

import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.dto.response.GameAutoCompleteResponse;
import com.ll.playon.domain.game.game.dto.response.GameDetailWithPartyResponse;
import com.ll.playon.domain.game.game.dto.response.GameSummaryResponse;
import com.ll.playon.domain.game.game.dto.response.PartySummaryResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.service.GameService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.steamAPI.SteamAPI;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "GameController")
public class GameController {
    private final SteamAPI steamAPI;

    private final GameService gameService;
    private final UserContext userContext;

    @GetMapping("/ranking")
    public RsData<List<GameListResponse>> getSteamGameTopFive() {
        return RsData.success(HttpStatus.OK, gameService.getGameRanking());
    }

    @GetMapping("/recommend")
    public RsData<List<GameListResponse>> getRecommendedGames() {
        Member actor = userContext.getActor();
        if(ObjectUtils.isEmpty(actor)) throw ErrorCode.UNAUTHORIZED.throwServiceException();
        return RsData.success(HttpStatus.OK, gameService.getGameRecommendations(actor));
    }

    // 테스트용 엔드포인트
    @GetMapping("/{appid}")
    public RsData<SteamGame> getGameDetail(@PathVariable Long appid) {
        return RsData.success(HttpStatus.OK,steamAPI.fetchOrCreateGameDetail(appid));
    }

    @GetMapping("/{appid}/details")
    public RsData<GameDetailWithPartyResponse> getGameDetail(
            @PathVariable Long appid,
            @PageableDefault(size = 3) Pageable partyPageable,
            @PageableDefault(size = 3) Pageable logPageable
    ) {
        return RsData.success(HttpStatus.OK, gameService.getGameDetailWithParties(appid, partyPageable, logPageable));
    }

    @GetMapping("/list")
    public RsData<PageDto<GameSummaryResponse>> getFilteredGames(
            @ModelAttribute GameSearchCondition condition,
            @PageableDefault(size = 12) Pageable pageable
    ) {
        return RsData.success(HttpStatus.OK, gameService.searchGames(condition, pageable));
    }

    @GetMapping("/search")
    public RsData<List<GameAutoCompleteResponse>> searchGameByKeyword(@RequestParam String keyword) {
        return RsData.success(HttpStatus.OK, gameService.autoCompleteGames(keyword));
    }

    @GetMapping("/{appid}/party")
    public RsData<PageDto<PartySummaryResponse>> getGameParties(
            @PathVariable Long appid,
            @PageableDefault(size = 12, sort = "partyAt") Pageable pageable
    ) {
        return RsData.success(HttpStatus.OK, gameService.getGameParties(appid, pageable));
    }

}
