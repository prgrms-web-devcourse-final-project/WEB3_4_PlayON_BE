package com.ll.playon.domain.game.game.service;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.global.steamAPI.SteamAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final SteamAPI steamAPI;

    private static final int TOP_FIVE = 5;

    public List<GameListResponse> makeGameList(List<Long> appIds) {
        final List<GameListResponse> responses = new ArrayList<>();

        final List<SteamGame> gameList = gameRepository.findAllByAppidIn(appIds);
        for (SteamGame game : gameList) {
            List<String> genres = game.getGenres().stream()
                    .map(SteamGenre::getName)
                    .toList();

            responses.add(GameListResponse.builder()
                    .appid(game.getAppid())
                    .name(game.getName())
                    .headerImage(game.getHeaderImage())
                    .gameGenres(genres)
                    .build());
        }

        return responses;
    }

    // 메인 페이지에 보여줄 스팀 랭킹
    public List<GameListResponse>  getGameRanking() {
        // 게임 리스트 id 불러오기
        final List<Long> steamRankingIds = steamAPI.getSteamRanking();

        // 리스트에 없는 게임 DB 추가
        updateGameDB(steamRankingIds);

        // 장르 추가 후 응답
        return makeGameList(steamRankingIds.stream().limit(TOP_FIVE).toList());
    }

    // 메인 페이지에 보여줄 사용자 게임 추천
    public void getGameRecommendations() {
        // 게임 리스트 id 불러오기

        // 장르 추가 후 리스트 완성

        // 사용자 선호 장르 필터링

        // 사용자가 소유하지 않은 게임 필터링

        // 응답
    }

    private void updateGameDB(List<Long> appIds) {
        // 인기게임 중에 DB에 없는 게임 추가
    }
}
