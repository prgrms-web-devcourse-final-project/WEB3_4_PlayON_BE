package com.ll.playon.domain.game.game.controller;

import com.ll.playon.domain.game.game.dto.response.GameDetailWithPartyResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.service.GameService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.steamAPI.SteamAPI;
import com.ll.playon.global.security.UserContext;
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
}
