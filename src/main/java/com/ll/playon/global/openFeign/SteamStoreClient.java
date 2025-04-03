package com.ll.playon.global.openFeign;

import com.ll.playon.global.openFeign.dto.SteamGameDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steamStoreClient", url = "https://store.steampowered.com")
public interface SteamStoreClient {

    @GetMapping("/api/appdetails")
    SteamGameDetailResponse getGameDetail(
            @RequestParam("appids") Integer appId,
            @RequestParam("cc") String countryCode,
            @RequestParam("filters") String filters
    );
}