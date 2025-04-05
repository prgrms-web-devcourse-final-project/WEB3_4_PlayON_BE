package com.ll.playon.global.steamAPI;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.entity.SteamImage;
import com.ll.playon.domain.game.game.entity.SteamMovie;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.game.game.repository.GenreRepository;
import com.ll.playon.global.openFeign.SteamApiClient;
import com.ll.playon.global.openFeign.SteamStoreClient;
import com.ll.playon.global.openFeign.dto.*;
import com.ll.playon.global.openFeign.dto.gameDetail.GameDetail2;
import com.ll.playon.global.openFeign.dto.gameDetail.Screenshot;
import com.ll.playon.global.openFeign.dto.gameDetail.SteamGameDetailResponse2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SteamAPI {
    private final SteamApiClient steamApiClient;
    private final SteamStoreClient steamStoreClient;
    private final GenreRepository genreRepository;
    private final GameRepository gameRepository;

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

    public List<Long> getSteamRanking() {
        final List<Long> result = new ArrayList<>();

        final List<GameItem> list = steamStoreClient.getGameRanking().getItems().stream().limit(10).toList();

        for (GameItem game : list) {
            String img = game.getLogo();

            // img 에서 appId 파싱
            int startIndex = img.indexOf("/apps/") + 6;
            int endIndex = img.indexOf("/", startIndex);
            String appId = img.substring(startIndex, endIndex);

            result.add(Long.valueOf(appId));
        }
        return result;
    }

    public SteamGame fetchOrCreateGameDetail(Long appId) {
        final Optional<SteamGame> steamGameOptional = gameRepository.findByAppid(appId);
        if(steamGameOptional.isPresent()) return steamGameOptional.get();

        final SteamGameDetailResponse2 response = steamStoreClient.getGameDetail(String.valueOf(appId));
        final GameDetail2 gameData = response.getGames().get(String.valueOf(appId)).getGameData();

        // releaseDate, LocalDate 로
        final String dateString = gameData.getRelease_date().getDate();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.ENGLISH);
        final LocalDate date = LocalDate.parse(dateString, formatter);

        // requiredAge, 나이 계산
        final int age;
        if (gameData.getRequired_age() >= 18) {
            age = 18;
        } else if (gameData.getRequired_age() >= 15) {
            age = 15;
        } else {
            age = 0;
        }

        // multiplayer, 카테고리에서
        boolean isMultiplayer = gameData.getCategories().stream()
                .anyMatch(category ->
                        category.getDescription().equalsIgnoreCase("Multi-player"));
        boolean isSingleplayer = gameData.getCategories().stream()
                .anyMatch(category ->
                        category.getDescription().equalsIgnoreCase("Single-player"));

        // screenshot, 리스트로
        List<String> screenshotList = gameData.getScreenshots().stream()
                .map(Screenshot::getPath_full).toList();

        // movie, 리스트로
        List<String> movieList = gameData.getMovies().stream()
                .map(movie -> movie.getMp4().getMax()).toList();

        // genre, 리스트로
        List<SteamGenre> genreList = gameData.getGenres().stream()
                .map(Genre::getDescription)
                .map(name -> genreRepository.findByName(name)
                        .orElseGet(() -> SteamGenre.builder().name(name).build()))
                .toList();

        SteamGame newGame = SteamGame.builder()
                .appid(appId)
                .name(gameData.getName())
                .releaseDate(date)
                .headerImage(gameData.getHeader_image())
                .requiredAge(age)
                .aboutTheGame(gameData.getAbout_the_game())
                .shortDescription(gameData.getShort_description())
                .website(gameData.getWebsite())
                .isWindowsSupported(gameData.getPlatforms().isWindows())
                .isMacSupported(gameData.getPlatforms().isMac())
                .isLinuxSupported(gameData.getPlatforms().isLinux())
                .isSinglePlayer(isSingleplayer)
                .isMultiPlayer(isMultiplayer)
                .developers(String.join(", ",gameData.getDevelopers()))
                .publishers(String.join(", ",gameData.getPublishers()))
                .genres(genreList)
                .build();
        newGame.getScreenshots().addAll(screenshotList.stream()
                .map(url -> SteamImage.builder().screenshot(url).game(newGame).build())
                .toList());
        newGame.getMovies().addAll(movieList.stream()
                .map(url -> SteamMovie.builder().movie(url).game(newGame).build())
                .toList());

        return gameRepository.save(newGame);
    }
}
