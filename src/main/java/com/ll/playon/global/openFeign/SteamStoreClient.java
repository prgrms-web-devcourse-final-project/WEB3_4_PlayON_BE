package com.ll.playon.global.openFeign;

import com.ll.playon.global.openFeign.dto.ranking.SteamSearchResponse;
import com.ll.playon.global.openFeign.dto.gameDetail.SteamGameDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steamStoreClient", url = "https://store.steampowered.com")
public interface SteamStoreClient {

    @GetMapping("/search/results/?json=1&category1=998&tags=3859")
    SteamSearchResponse getGameRanking();

    @GetMapping("/api/appdetails")
    SteamGameDetailResponse getGameDetail(
            @RequestParam("appids") String appId
    );
}