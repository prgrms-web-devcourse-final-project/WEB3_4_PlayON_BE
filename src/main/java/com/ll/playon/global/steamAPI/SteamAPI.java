package com.ll.playon.global.steamAPI;

import com.ll.playon.global.openFeign.SteamApiClient;
import com.ll.playon.global.openFeign.dto.Player;
import com.ll.playon.global.openFeign.dto.SteamGameResponse;
import com.ll.playon.global.openFeign.dto.SteamResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SteamAPI {
    private final SteamApiClient steamApiClient;

    // TODO : 스팀 API 장애 대응

    @Value("${custom.steam.apikey}")
    private String apikey;

    public Map<String, String> getUserProfile(Long steamId) {
        SteamResponse steamResponse = steamApiClient.getPlayerSummaries(apikey, String.valueOf(steamId));
        Player player = steamResponse.getResponse().getPlayers().getFirst();
        Map<String, String> response = new HashMap<>();

        response.put("nickname", player.getNickname());
        response.put("profileImg", player.getAvatar());

        return response;
    }

    public List<Long> getUserGames(Long steamId) {
        SteamGameResponse steamGameResponse = steamApiClient.getPlayerOwnedGames(apikey, String.valueOf(steamId));
        return steamGameResponse.getResponse().getGames().stream()
                .map(game -> Long.valueOf(game.getAppId()))
                .toList();
    }
}
