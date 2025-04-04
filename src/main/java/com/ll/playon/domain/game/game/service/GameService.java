package com.ll.playon.domain.game.game.service;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public List<GameListResponse> getGameList(List<Long> appIds) {
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
}
