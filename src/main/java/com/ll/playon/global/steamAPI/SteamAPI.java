package com.ll.playon.global.steamAPI;

import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GenreRepository;
import com.ll.playon.global.openFeign.SteamApiClient;
import com.ll.playon.global.openFeign.SteamStoreClient;
import com.ll.playon.global.openFeign.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SteamAPI {
    private final SteamApiClient steamApiClient;
    private final SteamStoreClient steamStoreClient;
    private final GenreRepository genreRepository;

    private static final int MAX_GAMES_TO_ANALYZE = 30;

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

    public SteamGenre getPreferredGenre(List<Long> userGames) {
        Map<String, Integer> genreCountMap = new HashMap<>();

        int count = 0;

        for(Long gameId : userGames) {
            String appId = String.valueOf(gameId);
            if(count++ > MAX_GAMES_TO_ANALYZE) break;

            // TODO : DB 조회로
            SteamGameDetailResponse response = steamStoreClient.getGameDetail(
                    Integer.valueOf(appId), "KR", "genres");

            GameDetailWrapper gameDetailWrapper = response.getGames().get(appId);
            if (gameDetailWrapper == null || !gameDetailWrapper.isSuccess()) continue;
            if (response.getGames().get(appId).getGameData() == null) continue;

            List<Genre> genres = response.getGames().get(appId).getGameData().getGenres();
            for (Genre genre : genres) {
                genreCountMap.put(genre.getDescription(), genreCountMap.getOrDefault(genre.getDescription(), 0) + 1);
            }
        }

        String preferredGenre = genreCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Optional<SteamGenre> genre = genreRepository.findByName(preferredGenre);
        return genre.orElseGet(() -> genreRepository.save(SteamGenre.builder().name(preferredGenre).build()));
    }
}
