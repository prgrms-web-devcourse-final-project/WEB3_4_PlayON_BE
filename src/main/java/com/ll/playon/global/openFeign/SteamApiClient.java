package com.ll.playon.global.openFeign;

import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.openFeign.dto.ownedGames.SteamGameResponse;
import com.ll.playon.global.openFeign.dto.playerSummaries.SteamResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Steam API 기본 URL 설정
@FeignClient(name = "steamApiClient", url = "https://api.steampowered.com")
public interface SteamApiClient {

    Logger log = LoggerFactory.getLogger(SteamApiClient.class);

    // GET 요청을 해당 엔드포인트로 보냄
    @GetMapping("/ISteamUser/GetPlayerSummaries/v2/")
    @Retry(name = "steamApiRetry")
    @RateLimiter(name = "steamApiRateLimiter")
    @CircuitBreaker(name = "steamApiCircuitBreaker", fallbackMethod = "fallbackPlayerSummary")
    SteamResponse getPlayerSummaries(
            @RequestParam("key") String apiKey,
            @RequestParam("steamids") String steamIds
    );

    @GetMapping("IPlayerService/GetOwnedGames/v1/")
    @Retry(name = "steamApiRetry")
    @RateLimiter(name = "steamApiRateLimiter")
    @CircuitBreaker(name = "steamApiCircuitBreaker", fallbackMethod = "fallbackOwnedGames")
    SteamGameResponse getPlayerOwnedGames(
            @RequestParam("key") String apiKey,
            @RequestParam("steamid") String steamId
    );

    default SteamGameResponse fallbackOwnedGames(String apiKey, String steamId, Throwable t) {
        log.warn("GetOwnedGames fallback: {}", t.getMessage());
        if (t instanceof CallNotPermittedException) {
            throw ErrorCode.STEAM_UNAVAILABLE.throwServiceException();
        } else if (t instanceof RequestNotPermitted) {
            throw ErrorCode.STEAM_TOO_MANY_REQUEST.throwServiceException();
        } else {
            throw ErrorCode.STEAM_NOT_RESPONDED.throwServiceException();
        }
    }

    default SteamResponse fallbackPlayerSummary(String apiKey, String steamIds, Throwable t) {
        log.warn("GetPlayerSummaries fallback: {}", t.getMessage());
        if (t instanceof CallNotPermittedException) {
            throw ErrorCode.STEAM_UNAVAILABLE.throwServiceException();
        } else if (t instanceof RequestNotPermitted) {
            throw ErrorCode.STEAM_TOO_MANY_REQUEST.throwServiceException();
        } else {
            throw ErrorCode.STEAM_NOT_RESPONDED.throwServiceException();
        }
    }
}